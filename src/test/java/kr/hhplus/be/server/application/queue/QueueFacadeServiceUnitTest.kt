package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.KSelect.Companion.field
import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueActiveStatus
import kr.hhplus.be.server.domain.queue.QueueService
import org.assertj.core.api.Assertions.assertThat
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class QueueFacadeServiceUnitTest {

	@InjectMocks
	private lateinit var queueFacadeService: QueueFacadeService

	@Mock
	private lateinit var queueService: QueueService

	@Mock
	private lateinit var clockHolder: ClockHolder

	@Test
	fun `대기 정보 조회 시, 이미 활성화된 토큰이라면 다른 조회 없이 바로 대기번호 0, 대기시간 0을 반환한다`() {
		// given
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()

		// when
		val actual = queueFacadeService.getWaitingInfo(queue)

		//then
		verifyNoInteractions(queueService)

		assertThat(actual.myWaitingOrder).isEqualTo(0)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(0)
	}
}