package kr.hhplus.be.server.interfaces.concert

data class ConcertInformationResponse(
    val concerts: MutableList<ConcertInformationDto>
)

data class ConcertInformationDto(
    val concertId: Long,
    val title: String,
    val provider: String
)