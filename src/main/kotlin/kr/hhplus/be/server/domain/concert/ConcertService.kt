package kr.hhplus.be.server.domain.concert

import org.springframework.stereotype.Service

@Service
class ConcertService(
	private val concertRepository: ConcertRepository
) {

	fun getConcertInformation(): List<ConcertInfo.Concert> {
		val concerts = concertRepository.findAllConcert(false)

		return concerts.map { ConcertInfo.Concert.from(it) }
	}
}