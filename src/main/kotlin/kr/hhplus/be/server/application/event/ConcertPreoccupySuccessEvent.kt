package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.concert.ConcertPreoccupyPayload
import java.time.LocalDateTime

data class ConcertPreoccupySuccessEvent(
	val traceId: String,
	val concertId: Long,
	val concertScheduleId: Long,
	val concertSeatId: Long,
	val expiredAt: LocalDateTime?,
	val originExpiredAt: LocalDateTime?
) {

	fun toConcertPreoccupyPayload(): ConcertPreoccupyPayload {
		return ConcertPreoccupyPayload(
			traceId = traceId,
			concertId = concertId,
			concertScheduleId = concertScheduleId,
			concertSeatId = concertSeatId,
			expiredAt = expiredAt,
			originExpiredAt = originExpiredAt
		)
	}
}
