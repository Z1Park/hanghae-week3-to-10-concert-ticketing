package kr.hhplus.be.server.domain.reservation

import java.time.LocalDateTime

class ReservationCommand {

	data class Create(
		val price: Int,
		val userId: Long,
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long,
	) {

		fun toReservation(expiredAt: LocalDateTime): Reservation =
			Reservation(expiredAt, price, userId, concertId, concertScheduleId, concertSeatId)
	}
}