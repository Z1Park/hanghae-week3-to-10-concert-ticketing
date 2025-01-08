package kr.hhplus.be.server.domain.queue

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class QueueService(
	private val queueRepository: QueueRepository
) {

	companion object {
		const val ACTIVATE_COUNT_PER_SEC = 80
	}

	fun getByUuid(tokenUUID: String): Queue = queueRepository.findByUUID(tokenUUID)
		?: throw EntityNotFoundException.fromParam("Queue", "uuid", tokenUUID)

	fun findLastActivatedQueue(): Queue? {
		val pageable = PageRequest.of(0, 1)
		val allActivatedQueue = queueRepository.findAllFromLastActivatedQueue(pageable)
		return allActivatedQueue.maxByOrNull { it.createdAt }
	}

	fun calculateWaitingInfo(queue: Queue, lastActivatedQueue: Queue?): QueueWaitingInfo {
		val waitingOrder = queue.calculateWaitingOrder(lastActivatedQueue)
		val expectedWaitingSeconds = ceil(waitingOrder.toDouble() / ACTIVATE_COUNT_PER_SEC).toInt()

		return QueueWaitingInfo(waitingOrder, expectedWaitingSeconds)
	}

	fun createNewQueueToken(userUUID: String, tokenUUID: String): Queue {
		val queue = Queue.createNewToken(userUUID, tokenUUID)
		return queueRepository.save(queue)
	}
}