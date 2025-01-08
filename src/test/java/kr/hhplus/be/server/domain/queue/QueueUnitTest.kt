package kr.hhplus.be.server.domain.queue

import kr.hhplus.be.server.domain.KSelect.Companion.field
import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class QueueUnitTest {

	@Test
	fun `대기열 토큰 생성 시, userUUID와 새로운 tokenUUID를 가진 Queue 객체를 반환한다`() {
		// given
		val userUUID = "myUserUUID"
		val tokenUUID = "newTokenUUID"

		// when
		val actual = Queue.createNewToken(userUUID, tokenUUID)

		//then
		assertThat(actual.userUUID).isEqualTo("myUserUUID")
		assertThat(actual.tokenUUID).isEqualTo("newTokenUUID")
	}

	@Test
	fun `대기번호 계산 시, 최근 활성 토큰이 null이라면 id만큼 대기번호를 반환한다`() {
		// given
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 153L)
			.create()

		// when
		val actual = queue.calculateWaitingOrder(null)

		//then
		assertThat(actual).isEqualTo(152L)
	}

	@Test
	fun `대기번호 계산 시, 최근 활성 토큰의 id가 조회 토큰보다 큰 경우 0을 반환한다`() {
		// given
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 154L)
			.create()
		val lastActivatedQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 155L)
			.create()

		// when
		val actual = queue.calculateWaitingOrder(lastActivatedQueue)

		//then
		assertThat(actual).isEqualTo(0)
	}

	@Test
	fun `토큰 활성화 요청 시, 대기 중인 토큰을 활성 상태로 바꾼다`() {
		// given
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.WAITING)
			.create()
		val testTime = LocalDateTime.of(2025, 1, 7, 13, 56)

		// when
		queue.activate(testTime)

		//then
		assertThat(queue.activateStatus).isEqualTo(QueueActiveStatus.ACTIVATED)
		assertThat(queue.expiredAt).isEqualTo(testTime.plusMinutes(10))
	}

	@Test
	fun `토큰 활성화 요청 시, 만료된 토큰은 활성화 되지 않는다`() {
		// given
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.DEACTIVATED)
			.set(field(Queue::expiredAt), null)
			.create()
		val testTime = LocalDateTime.of(2025, 1, 7, 13, 56)

		// when
		queue.activate(testTime)

		//then
		assertThat(queue.activateStatus).isEqualTo(QueueActiveStatus.DEACTIVATED)
		assertThat(queue.expiredAt).isNull()
	}
}