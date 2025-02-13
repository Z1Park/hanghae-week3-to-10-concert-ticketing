package kr.hhplus.be.server.application.event

import java.time.LocalDateTime

data class ReservationConcertSeatFailEvent(
	val concertSeatId: Long,
	val originExpiredAt: LocalDateTime?
)
