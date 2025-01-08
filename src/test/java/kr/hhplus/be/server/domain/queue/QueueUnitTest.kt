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
	fun `대기열 토큰 생성 시, userUUID와 30분의 만료시간, 새로운 tokenUUID를 가진 Queue 객체를 반환한다`() {
		// given
		val userUUID = "myUserUUID"
		val tokenUUID = "newTokenUUID"
		val testTime = LocalDateTime.of(2025, 1, 7, 10, 56)

		// when
		val actual = Queue.createNewToken(userUUID, tokenUUID) { testTime }

		//then
		assertThat(actual.userUUID).isEqualTo("myUserUUID")
		assertThat(actual.expiredAt).isEqualTo(LocalDateTime.of(2025, 1, 7, 11, 26))
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
}