package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.concert.ConcertSeatSoldOutPayload
import java.time.LocalDateTime

data class ConcertMakeSoldOutEvent(
	val traceId: String,
	val concertSeatId: Long,
	val originExpiredAt: LocalDateTime?
) {

	fun toConcertSeatSoldOutPayload(): ConcertSeatSoldOutPayload =
		ConcertSeatSoldOutPayload(traceId, concertSeatId, originExpiredAt)
}
