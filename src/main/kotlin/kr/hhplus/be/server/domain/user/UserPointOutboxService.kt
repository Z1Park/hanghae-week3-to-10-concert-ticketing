package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.user.UserPointOutboxRepository
import kr.hhplus.be.server.infrastructure.user.entity.UserPointOutboxMessage
import org.springframework.stereotype.Service

@Service
class UserPointOutboxService(
	private val userPointOutboxRepository: UserPointOutboxRepository
) {

	fun savePointUseInfo(payload: PointUsePayload) {
		val outboxMessage = UserPointOutboxMessage(
			payload.traceId,
			OutboxEventType.PAY,
			OutboxEventStatus.PROCESSED,
			payload.userId,
			payload.originBalance,
			payload.pointHistoryId
		)
		userPointOutboxRepository.save(outboxMessage)
	}

	fun processRollbackUsePoint(traceId: String): PointUsePayload {
		val outboxMessage = userPointOutboxRepository.findByTraceId(traceId)
		require(outboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}

		outboxMessage.updateStatusRollbacked()
		userPointOutboxRepository.save(outboxMessage)

		return PointUsePayload(
			outboxMessage.traceId,
			outboxMessage.userId,
			outboxMessage.originBalance,
			outboxMessage.pointHistoryId
		)
	}
}