package kr.hhplus.be.server.interfaces.concert.dto

import kr.hhplus.be.server.domain.concert.ConcertInfo

data class TopConcertInformationResponse(
	val concerts: List<TopConcertInformationDto>?
) {
	companion object {
		fun from(concertInfos: List<ConcertInfo.ConcertDto>?): TopConcertInformationResponse =
			TopConcertInformationResponse(concertInfos?.map { TopConcertInformationDto.from(it) })
	}
}

data class TopConcertInformationDto(
	val concertId: Long,
	val title: String,
	val provider: String,
	val finished: Boolean
) {
	companion object {
		fun from(concertInfo: ConcertInfo.ConcertDto): TopConcertInformationDto =
			TopConcertInformationDto(concertInfo.id, concertInfo.title, concertInfo.provider, concertInfo.finished)
	}
}