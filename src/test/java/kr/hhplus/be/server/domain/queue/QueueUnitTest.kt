package kr.hhplus.be.server.domain.queue

import org.assertj.core.api.Assertions.assertThat
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
}