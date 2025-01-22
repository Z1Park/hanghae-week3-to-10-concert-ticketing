package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.domain.queue.QueueWaitingInfo
import org.springframework.stereotype.Service

@Service
class QueueFacadeService(
	private val queueService: QueueService,
	private val clockHolder: ClockHolder
) {

	fun getWaitingInfo(tokenUUID: String): QueueWaitingInfo {
		val queue = queueService.getByUuid(tokenUUID)
		val lastActivatedQueue = queueService.findLastActivatedQueue()

		return queueService.calculateWaitingInfo(queue, lastActivatedQueue)
	}

	fun issueQueueToken(userUUID: String, uuidGenerator: UuidGenerator): String {
		val generatedUuid = uuidGenerator.generateUuid()

		val createdQueue = queueService.createNewToken(userUUID, generatedUuid)
		return createdQueue.tokenUUID
	}

	fun refreshTokens() {
		queueService.expireTokens(clockHolder)

		queueService.activateTokens(clockHolder)
	}
}