package kr.hhplus.be.server.interfaces.reservation

import java.time.LocalDateTime

data class ReserveConcertResponse(
	val reservationId: Long,
	val reservationExpiredAt: LocalDateTime
)
