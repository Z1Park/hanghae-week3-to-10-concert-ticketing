package kr.hhplus.be.server.application.event

import java.time.LocalDateTime

data class ConcertSeatPreoccupySuccessEvent(
	val price: Int,
	val userId: Long,
	val concertId: Long,
	val concertScheduleId: Long,
	val concertSeatId: Long,
	val expiredAt: LocalDateTime,
	val originExpiredAt: LocalDateTime?
) {
}