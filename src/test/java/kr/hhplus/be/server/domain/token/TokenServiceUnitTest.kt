package kr.hhplus.be.server.domain.token

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.time.ZoneOffset

@ExtendWith(MockitoExtension::class)
class TokenServiceUnitTest {

	@InjectMocks
	private lateinit var sut: TokenService

	@Mock
	private lateinit var tokenRepository: TokenRepository

	@Test
	fun `대기번호 조회 시, 대기열 내 토큰의 랭크를 통해 대기번호와 예상 대기시간을 계산 후 반환한다`() {
		// given
		val tokenUUID = "myTokenUUID"
		val myRank = 58L

		`when`(tokenRepository.getWaitingTokenRank(tokenUUID))
			.then { myRank }

		// when
		val actual = sut.getWaitingInfo(tokenUUID)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(58L)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(3)
	}

	@Test
	fun `대기번호 조회 시, 대기 토큰에도 없고 활성화된 토큰도 아니라면 CustomException이 발생한다`() {
		// given
		val tokenUUID = "myTokenUUID"

		`when`(tokenRepository.getWaitingTokenRank(tokenUUID))
			.then { null }
		`when`(tokenRepository.isActiveTokenExist(tokenUUID))
			.then { false }

		// when then
		assertThatThrownBy { sut.getWaitingInfo(tokenUUID) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN)
	}

	@Test
	fun `대기번호 조회 시, 이미 활성화된 상태라면 0의 대기번호와 0의 예상 대기시간을 반환한다`() {
		// given
		val tokenUUID = "myTokenUUID"

		`when`(tokenRepository.getWaitingTokenRank(tokenUUID))
			.then { null }
		`when`(tokenRepository.isActiveTokenExist(tokenUUID))
			.then { true }

		// when
		val actual = sut.getWaitingInfo(tokenUUID)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(0)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(0)
	}

	@Test
	fun `대기 토큰 생성 시, 전달받은 UUID와 현재 시간을 통한 score를 계산하여 대기 토큰 저장하는 메서드를 호출한다`() {
		// given
		val testTIme = LocalDateTime.of(2025, 2, 6, 18, 5, 10)
		val tokenUUID = "myTokenUUID"

		// when
		sut.createWaitingToken(tokenUUID) { testTIme }

		//then
		val expectedScore = testTIme.toEpochSecond(ZoneOffset.UTC) + 3600
		verify(tokenRepository).createWaitingToken(tokenUUID, expectedScore.toDouble())
	}

	@Test
	fun `만료된 대기 토큰 삭제 요청 시, 현재 시간을 통해 score를 계산하여 해당 score 이전의 모든 대기 토큰을 삭제하는 메서드를 호출한다`() {
		// given
		val testTIme = LocalDateTime.of(2025, 2, 6, 18, 5, 10)

		// when
		sut.removeExpiredWaitingTokens() { testTIme }

		//then
		val expectedScore = testTIme.toEpochSecond(ZoneOffset.UTC).toDouble()
		verify(tokenRepository).removeWaitingTokenScoreRange(expectedScore)
	}

	@Test
	fun `토큰 활성화 요청 시, 대기 토큰을 조회하여 활성화 토큰으로 생성 후 대기 토큰에서 일괄 삭제시킨다`() {
		// given
		val token1 = "tokenUUID1"
		val token2 = "tokenUUID2"
		val token3 = "tokenUUID3"
		val waitingTokens = listOf(token1, token2, token3)

		`when`(tokenRepository.getWaitingTokenRange(24))
			.then { waitingTokens }


		// when
		sut.activateTokens()

		//then
		verify(tokenRepository).removeWaitingTokenRankRange(2L)
	}

	@Test
	fun `대기 토큰 검증 시, 대기 토큰 저장소에 해당 토큰이 존재하면 검증에 성공한다`() {
		// given
		val token = "myTokenUUID"

		`when`(tokenRepository.isWaitingTokenExist(token))
			.then { true }

		// when then
		assertDoesNotThrow { sut.validateWaitingToken(token) }
	}

	@Test
	fun `대기 토큰 검증 시, 대기 토큰 저장소에 해당 토큰이 없으면 CustomException이 발생한다`() {
		// given
		val token = "noneTokenUUID"

		`when`(tokenRepository.isWaitingTokenExist(token))
			.then { false }

		// when then
		assertThatThrownBy { sut.validateWaitingToken(token) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INVALID_QUEUE_TOKEN)
	}

	@Test
	fun `활성화 토큰 검증 시, 활성화 토큰 저장소에 해당 토큰이 있으면 검증에 성공한다`() {
		// given
		val token = "myTokenUUID"

		`when`(tokenRepository.isActiveTokenExist(token))
			.then { true }

		// when then
		assertDoesNotThrow { sut.validateActiveToken(token) }
	}

	@Test
	fun `활성화 토큰 검증 시, 활성화 토큰 저장소에 해당 토큰이 없으면 CustomException이 발생한다`() {
		// given
		val token = "noneTokenUUID"

		`when`(tokenRepository.isActiveTokenExist(token))
			.then { false }

		// when then
		assertThatThrownBy { sut.validateActiveToken(token) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.REQUIRE_ACTIVATED_QUEUE_TOKEN)
	}
}