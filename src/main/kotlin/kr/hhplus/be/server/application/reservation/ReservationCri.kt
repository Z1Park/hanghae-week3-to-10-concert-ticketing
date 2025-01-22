package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.domain.concert.ConcertCommand

class ReservationCri {

	data class Create(
		val userUUID: String,
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long,
	) {
		fun toConcertCommandTotal(): ConcertCommand.Reserve =
			ConcertCommand.Reserve(concertId, concertScheduleId, concertSeatId)
	}
}