package kr.hhplus.be.server.domain.concert

import java.time.LocalDateTime

data class ConcertSeatSoldOutPayload(
	val traceId: String,
	val concertSeatId: Long,
	val originExpiredAt: LocalDateTime?
)
