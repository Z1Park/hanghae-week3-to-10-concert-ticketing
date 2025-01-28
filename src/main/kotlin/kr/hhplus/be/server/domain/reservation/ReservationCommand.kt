package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.domain.reservation.model.Reservation
import java.time.LocalDateTime

class ReservationCommand {

	data class Create(
		val price: Int,
		val userId: Long,
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long,
		val expiredAt: LocalDateTime
	) {

		fun toReservation(): Reservation =
			Reservation(expiredAt, price, userId, concertId, concertScheduleId, concertSeatId)
	}
}