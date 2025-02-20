package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationOutboxMessage

data class ReservationDataPlatformPayload(
	val traceId: String,
	val reservationId: Long,
	val userId: Long?,
	val concertSeatId: Long?,
	val price: Int?,
) {

	companion object {
		fun from(outboxMessage: ReservationOutboxMessage): ReservationDataPlatformPayload =
			ReservationDataPlatformPayload(
				outboxMessage.traceId,
				outboxMessage.reservationId,
				outboxMessage.userId,
				outboxMessage.concertSeatId,
				outboxMessage.price
			)
	}
}