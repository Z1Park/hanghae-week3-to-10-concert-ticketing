package kr.hhplus.be.server.domain.concert

class ConcertCommand {

	data class Preoccupy(
		val concertId: Long,
		val concertScheduleId: Long,
		val concertSeatId: Long
	)
}