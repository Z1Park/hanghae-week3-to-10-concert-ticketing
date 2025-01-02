package kr.hhplus.be.server.interfaces.reservation

data class ReserveConcertRequest(
    val concertId: Long,
    val concertScheduleId: Long,
    val concertSectionId: Long,
    val concertSeatId: Long
)
