package kr.hhplus.be.server.domain.reservation

import java.time.LocalDateTime

data class ReservationDataPlatformPayload(
	val traceId: String,
	val reservationId: Long,
	val userId: Long,
	val concertSeatId: Long,
	val price: Int,
	val reservedAt: LocalDateTime
)