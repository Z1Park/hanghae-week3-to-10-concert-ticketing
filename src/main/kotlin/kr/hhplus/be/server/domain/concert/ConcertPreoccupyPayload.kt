package kr.hhplus.be.server.domain.concert

import java.time.LocalDateTime

data class ConcertPreoccupyPayload(
	val traceId: String,
	val concertId: Long?,
	val concertScheduleId: Long?,
	val concertSeatId: Long,
	val expiredAt: LocalDateTime?,
	val originExpiredAt: LocalDateTime?
)

