package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.concert.ConcertOutboxRepository
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertOutboxMessage
import org.springframework.stereotype.Service

@Service
class ConcertOutboxService(
	private val concertOutboxRepository: ConcertOutboxRepository
) {

	fun saveConcertPreoccupyInfo(payload: ConcertPreoccupyPayload) {
		val concertOutboxMessage = ConcertOutboxMessage(
			payload.traceId,
			OutboxEventType.RESERVE,
			OutboxEventStatus.PROCESSED, // API 호출로 이루어지는 동작이기 때문에 PROCESSED로 저장
			payload.concertId,
			payload.concertScheduleId,
			payload.concertSeatId,
			payload.expiredAt,
			payload.originExpiredAt
		)
		concertOutboxRepository.save(concertOutboxMessage)
	}

	fun saveConcertSeatSoldOutInfo(payload: ConcertSeatSoldOutPayload) {
		val concertOutboxMessage = ConcertOutboxMessage(
			payload.traceId,
			OutboxEventType.RESERVE,
			OutboxEventStatus.PROCESSED, // API 호출로 이루어지는 동작이기 때문에 PROCESSED로 저장
			null,
			null,
			payload.concertSeatId,
			null,
			payload.originExpiredAt
		)
		concertOutboxRepository.save(concertOutboxMessage)
	}

	fun processRollbackConcertPreoccupy(traceId: String): ConcertPreoccupyPayload {
		val outboxMessage = concertOutboxRepository.findByTraceId(traceId)
		require(outboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}
		outboxMessage.updateStatusRollbacked()
		concertOutboxRepository.save(outboxMessage)

		return ConcertPreoccupyPayload(
			outboxMessage.traceId,
			outboxMessage.concertId,
			outboxMessage.concertScheduleId,
			outboxMessage.concertSeatId,
			outboxMessage.expiredAt,
			outboxMessage.originExpiredAt
		)
	}

	fun processRollbackConcertSeatSoldOut(traceId: String): ConcertSeatSoldOutPayload {
		val outboxMessage = concertOutboxRepository.findByTraceId(traceId)
		require(outboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}
		outboxMessage.updateStatusRollbacked()
		concertOutboxRepository.save(outboxMessage)

		return ConcertSeatSoldOutPayload(
			outboxMessage.traceId,
			outboxMessage.concertSeatId,
			outboxMessage.originExpiredAt
		)
	}
}