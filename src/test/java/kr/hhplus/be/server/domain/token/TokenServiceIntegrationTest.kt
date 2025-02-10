package kr.hhplus.be.server.domain.token

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootTest
class TokenServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: TokenService,
	@Autowired private val zSetOperations: ZSetOperations<String, String>,
	@Autowired private val valueOperations: ValueOperations<String, String>
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `대기번호 조회 시, 앞에 50명이 있다면 50의 대기번호를 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 6, 7, 33, 49)
		for (i in 0L until 50L) {
			val inputTime = testTime.plusNanos(i * 1000)
			val score = inputTime.toEpochSecond(ZoneOffset.UTC).toDouble()
			zSetOperations.add("waitingToken", "tokenUUID$i", score)
		}

		val tokenUUID = "myTokenUUID"
		val inputTime = testTime.plusMinutes(1L)
		val score = inputTime.toEpochSecond(ZoneOffset.UTC).toDouble()
		zSetOperations.add("waitingToken", tokenUUID, score)

		// when
		val actual = sut.getWaitingInfo(tokenUUID)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(50)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(2)
	}

	@Test
	fun `대기 토큰 생성 시, 현재 시간부터 1시간 후 만료 시간으로 score를 만들어 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 6, 8, 46, 42)
		val tokenUUID = "myTokenUUID"

		// when
		sut.createWaitingToken(tokenUUID) { testTime }

		//then
		val expectedScore = testTime.plusHours(1).toEpochSecond(ZoneOffset.UTC).toDouble()
		val actual = zSetOperations.score("waitingToken", "myTokenUUID")
		assertThat(actual).isNotNull()
		assertThat(actual).isEqualTo(expectedScore)
	}

	@Test
	fun `만료 대기 토큰 삭제 요청 시, 현재 시간 기준으로 score를 계산한 값보다 score가 작은 모든 대기 토큰을 삭제한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 23, 56, 46)
		for (i in 1L until 10L) {
			val inputTime = testTime.minusSeconds(i)
			val score = inputTime.toEpochSecond(ZoneOffset.UTC).toDouble()
			zSetOperations.add("waitingToken", "tokenUUID$i", score)
		}
		val score = testTime.toEpochSecond(ZoneOffset.UTC).toDouble()
		zSetOperations.add("waitingToken", "tokenUUID10", score)
		for (i in 11L until 21L) {
			val inputTime = testTime.plusSeconds((i - 10))
			val score = inputTime.toEpochSecond(ZoneOffset.UTC).toDouble()
			zSetOperations.add("waitingToken", "tokenUUID$i", score)
		}

		// when
		sut.removeExpiredWaitingTokens() { testTime }

		//then
		val size = zSetOperations.size("waitingToken")
		assertThat(size).isEqualTo(10)

		for (i in 1L until 10L) {
			val waitingToken = zSetOperations.score("waitingToken", "tokenUUID$i")
			assertThat(waitingToken).isNull()
		}
	}

	@Test
	fun `토큰 활성화 요청 시, 40명의 대기 토큰 중 가장 먼저 들어온 25명이 활성 토큰으로 저장된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 10, 49, 56)
		for (i in 1L until 40L) {
			val inputTime = testTime.plusSeconds(i)
			val score = inputTime.toEpochSecond(ZoneOffset.UTC).toDouble()
			zSetOperations.add("waitingToken", "tokenUUID$i", score)
		}

		// when
		sut.activateTokens()

		//then
		for (i in 1L..25L) {
			val token = "tokenUUID$i"

			val waitingToken = zSetOperations.score("waitingToken", token)
			assertThat(waitingToken).isNull()

			val activeToken = valueOperations.get(token)
			assertThat(activeToken).isNotNull()
		}

		for (i in 26L until 40L) {
			val waitingToken = zSetOperations.score("waitingToken", "tokenUUID$i")
			assertThat(waitingToken).isNotNull()
		}
	}

	@Test
	fun `대기 토큰 검증 시, 해당 UUID가 대기 토큰 저장소에 있다면 성공한다`() {
		// given
		val tokenUUID = "myTokenUUID"
		zSetOperations.add("waitingToken", tokenUUID, 1.0)

		// when then
		assertDoesNotThrow { sut.validateWaitingToken(tokenUUID) }
	}

	@Test
	fun `대기 토큰 검증 시, 해당 UUID가 대기 토큰 저장소에 없다면 CustomException이 발생한다`() {
		// given
		val tokenUUID = "myTokenUUID"

		// when then
		assertThatThrownBy { sut.validateWaitingToken(tokenUUID) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN)
	}

	@Test
	fun `활성화 토큰 검증 시, 해당 UUID가 활성화 토큰 저장소에 있다면 성공한다`() {
		// given
		val tokenUUID = "myTokenUUID"
		valueOperations.set(tokenUUID, "myValue")

		// when then
		assertDoesNotThrow { sut.validateActiveToken(tokenUUID) }
	}

	@Test
	fun `활성화 토큰 검증 시, 해당 UUID가 활성화 토큰 저장소에 없다면 CustomException이 발생한다`() {
		// given
		val tokenUUID = "myTokenUUID"

		// when then
		assertThatThrownBy { sut.validateActiveToken(tokenUUID) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.REQUIRE_ACTIVATED_QUEUE_TOKEN)
	}
}