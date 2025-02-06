package kr.hhplus.be.server.interfaces.concert.dto

import kr.hhplus.be.server.domain.concert.ConcertInfo

data class ConcertInformationResponse(
	val concerts: List<ConcertInformationDto>
) {
	companion object {
		fun from(concertInfos: List<ConcertInfo.ConcertDto>): ConcertInformationResponse =
			ConcertInformationResponse(concertInfos.map { ConcertInformationDto.from(it) })
	}
}

data class ConcertInformationDto(
	val concertId: Long,
	val title: String,
	val provider: String
) {
	companion object {
		fun from(concertInfo: ConcertInfo.ConcertDto): ConcertInformationDto =
			ConcertInformationDto(concertInfo.id, concertInfo.title, concertInfo.provider)
	}
}