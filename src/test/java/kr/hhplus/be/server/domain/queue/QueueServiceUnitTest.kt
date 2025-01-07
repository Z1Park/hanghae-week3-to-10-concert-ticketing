package kr.hhplus.be.server.domain.queue

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class QueueServiceUnitTest {

	@InjectMocks
	private lateinit var sut: QueueService

	@Mock
	private lateinit var queueRepository: QueueRepository

}