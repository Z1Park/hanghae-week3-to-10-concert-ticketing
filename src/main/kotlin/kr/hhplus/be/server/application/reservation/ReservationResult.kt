package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.domain.reservation.model.Reservation
import java.time.LocalDateTime

data class ReservationResult(
	val reservationId: Long,
	val userId: Long,
	val seatId: Long,
	val reservationExpiredAt: LocalDateTime
) {
	companion object {
		fun from(reservation: Reservation): ReservationResult =
			ReservationResult(
				reservationId = reservation.id,
				userId = reservation.userId,
				seatId = reservation.concertSeatId,
				reservationExpiredAt = reservation.expiredAt!!
			)
	}
}
