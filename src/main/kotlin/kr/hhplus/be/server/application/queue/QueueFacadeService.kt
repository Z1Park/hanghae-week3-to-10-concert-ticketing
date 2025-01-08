package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.domain.queue.QueueWaitingInfo
import org.springframework.stereotype.Service

@Service
class QueueFacadeService(
	private val queueService: QueueService,
	private val clockHolder: ClockHolder
) {

	fun getWaitingInfo(queue: Queue): QueueWaitingInfo {
		if (queue.isActivated()) {
			return QueueWaitingInfo(0, 0);
		}

		val lastActivatedQueue = queueService.findLastActivatedQueue()
		return queueService.calculateWaitingInfo(queue, lastActivatedQueue)
	}

	fun issueQueueToken(userUUID: String, uuidGenerator: UuidGenerator): String {

		val generatedUuid = uuidGenerator.generateUuid()

		queueService.createNewQueueToken(userUUID, generatedUuid)

		return generatedUuid
	}

	fun activateTokens() {
		queueService.activateTokens(clockHolder)
	}
}