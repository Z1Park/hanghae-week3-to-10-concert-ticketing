package kr.hhplus.be.server.interfaces.concert

import java.time.ZonedDateTime

data class ConcertSectionResponse(
    val concertPlaceName: String,
    val concertPosition: String,
    val totalSeat: Int,
    val sections: MutableList<ConcertSectionDto>
)

data class ConcertSectionDto(
    val concertSectionId: Long,
    val sectionName: String,
    val totalSeat: Int,
    val remainSeat: Int
)
