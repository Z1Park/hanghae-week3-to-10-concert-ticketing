package kr.hhplus.be.server.interfaces.reservation

import java.time.ZonedDateTime

data class ReserveConcertResponse(
    val reservationId: Long,
    val reservationExpiredAt: ZonedDateTime
)
