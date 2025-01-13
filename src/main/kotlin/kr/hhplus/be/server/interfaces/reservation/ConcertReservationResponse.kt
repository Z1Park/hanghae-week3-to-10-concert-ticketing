package kr.hhplus.be.server.interfaces.reservation

import kr.hhplus.be.server.application.reservation.ReservationResult
import java.time.LocalDateTime

data class ConcertReservationResponse(
	val reservationId: Long,
	val reservationExpiredAt: LocalDateTime
) {
	companion object {
		fun from(reservationResult: ReservationResult): ConcertReservationResponse =
			ConcertReservationResponse(reservationResult.reservationId, reservationResult.reservationExpiredAt)
	}
}
