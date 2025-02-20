package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.reservation.ReservationOutboxRepository
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationOutboxMessage
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReservationOutboxService(
	private val reservationOutboxRepository: ReservationOutboxRepository
) {

	fun saveReservationDataPlatformMessage(payload: ReservationDataPlatformPayload) {
		val outboxMessage = ReservationOutboxMessage(
			payload.traceId,
			OutboxEventType.RESERVE,
			OutboxEventStatus.CREATED,
			payload.reservationId,
			payload.userId,
			payload.concertSeatId,
			payload.price,
			null
		)
		reservationOutboxRepository.save(outboxMessage)
	}

	fun saveReservationConfirmInfo(payload: ReservationConfirmPayload) {
		val outboxMessage = ReservationOutboxMessage(
			payload.traceId,
			OutboxEventType.RESERVE,
			OutboxEventStatus.PROCESSED,
			payload.reservationId,
			null,
			null,
			null,
			payload.originExpiredAt
		)
		reservationOutboxRepository.save(outboxMessage)
	}

	fun processReservationDataPlatformMessage(traceId: String) {
		val reservationOutboxMessage = reservationOutboxRepository.findByTraceId(traceId)
		require(reservationOutboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}

		reservationOutboxMessage.updateStatusProcessed()
		reservationOutboxRepository.save(reservationOutboxMessage)
	}

	fun findAllUnprocessedDataToResendDataPlatform(before: LocalDateTime): List<ReservationDataPlatformPayload> {
		val messages = reservationOutboxRepository.findAllByEventStatusAndCreatedAtBefore(OutboxEventStatus.CREATED, before)
		return messages.map { ReservationDataPlatformPayload.from(it) }
	}

	fun processRollbackReservationConfirm(traceId: String): ReservationConfirmPayload {
		val outboxMessage = reservationOutboxRepository.findByTraceId(traceId)
		require(outboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}

		outboxMessage.updateStatusRollbacked()
		reservationOutboxRepository.save(outboxMessage)

		return ReservationConfirmPayload(
			outboxMessage.traceId,
			outboxMessage.reservationId,
			outboxMessage.originExpiredAt
		)
	}
}