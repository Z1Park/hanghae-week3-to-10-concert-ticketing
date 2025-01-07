package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.domain.queue.QueueService
import org.springframework.stereotype.Service

@Service
class QueueFacadeService(
	private val queueService: QueueService,
	private val clockHolder: ClockHolder
) {

	fun issueQueueToken(userUUID: String, uuidGenerator: UuidGenerator): String {

		val generatedUuid = uuidGenerator.generateUuid()

		queueService.createNewQueueToken(userUUID, generatedUuid, clockHolder)

		return generatedUuid
	}
}