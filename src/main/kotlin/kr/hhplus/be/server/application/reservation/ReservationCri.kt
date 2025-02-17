package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.domain.reservation.ReservationCommand

class ReservationCri {

	data class Create(
		val userUUID: String,
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long,
	) {
		fun toReservationCommandCreate(): ReservationCommand.Create =
			ReservationCommand.Create(userUUID, concertId, concertScheduleId, concertSeatId)
	}
}