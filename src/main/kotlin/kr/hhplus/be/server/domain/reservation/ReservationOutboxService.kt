package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.reservation.ReservationOutboxRepository
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationOutboxMessage
import org.springframework.stereotype.Service

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
			payload.price
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
}