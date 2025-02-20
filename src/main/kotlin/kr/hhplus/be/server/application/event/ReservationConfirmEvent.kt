package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.reservation.ReservationConfirmPayload
import java.time.LocalDateTime

data class ReservationConfirmEvent(
	val traceId: String,
	val reservationId: Long,
	val originExpiredAt: LocalDateTime?
) {

	fun toReservationConfirmPayload(): ReservationConfirmPayload =
		ReservationConfirmPayload(traceId, reservationId, originExpiredAt)
}
