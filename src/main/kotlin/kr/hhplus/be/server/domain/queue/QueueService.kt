package kr.hhplus.be.server.domain.queue

import kr.hhplus.be.server.common.ClockHolder
import org.springframework.stereotype.Service

@Service
class QueueService(
	private val queueRepository: QueueRepository
) {

	fun createNewQueueToken(userUUID: String, tokenUUID: String, clockHolder: ClockHolder): Queue {
		val queue = Queue.createNewToken(userUUID, tokenUUID, clockHolder)
		return queueRepository.save(queue)
	}
}