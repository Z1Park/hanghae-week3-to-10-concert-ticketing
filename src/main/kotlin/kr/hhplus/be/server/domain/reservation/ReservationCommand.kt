package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.domain.concert.ConcertCommand

class ReservationCommand {

	data class Create(
		val userUUID: String,
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long
	) {
		fun toPreoccupyCommand(): ConcertCommand.Preoccupy =
			ConcertCommand.Preoccupy(concertId, concertScheduleId, concertSeatId)
	}
}