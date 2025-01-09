package kr.hhplus.be.server.domain.concert

class ConcertCommand {

	data class Total(
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long
	)
}