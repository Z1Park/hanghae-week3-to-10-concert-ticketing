package kr.hhplus.be.server.domain.concert

class ConcertCommand {

	data class Reserve(
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long,
		val userId: Long
	)
}