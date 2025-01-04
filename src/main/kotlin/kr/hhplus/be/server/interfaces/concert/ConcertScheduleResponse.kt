package kr.hhplus.be.server.interfaces.concert

import java.time.ZonedDateTime

data class ConcertScheduleResponse(
    val concertSchedules: MutableList<ConcertScheduleDto>
)

data class ConcertScheduleDto(
    val concertScheduleId: Long,
    val concertPlace: String,
    val concertLocation: String,
    val totalSeat: Int,
    val startAt: ZonedDateTime,
    val endAt: ZonedDateTime
)
