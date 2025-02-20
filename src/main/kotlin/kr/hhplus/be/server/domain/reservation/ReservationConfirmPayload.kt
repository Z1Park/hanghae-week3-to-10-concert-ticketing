package kr.hhplus.be.server.domain.reservation

import java.time.LocalDateTime

data class ReservationConfirmPayload(
	val traceId: String,
	val reservationId: Long,
	val originExpiredAt: LocalDateTime?
)