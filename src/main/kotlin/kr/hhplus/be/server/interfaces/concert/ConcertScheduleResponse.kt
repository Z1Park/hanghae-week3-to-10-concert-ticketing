package kr.hhplus.be.server.interfaces.concert

import java.time.ZonedDateTime

data class ConcertScheduleResponse(
    val concertSchedules: MutableList<ConcertScheduleDto>
)

data class ConcertScheduleDto(
    val concertScheduleId: Long,
    val startAt: ZonedDateTime,
    val endAt: ZonedDateTime
)
