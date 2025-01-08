package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class ConcertService(
	private val concertRepository: ConcertRepository
) {

	fun getConcertInformation(): List<ConcertInfo.Concert> {
		val concerts = concertRepository.findAllConcert(false)

		return concerts.map { ConcertInfo.Concert.from(it) }
	}

	fun getConcertSchedule(concertId: Long): List<ConcertInfo.Schedule> {
		val concert = concertRepository.findConcertWithSchedule(concertId)
			?: throw EntityNotFoundException.fromId("Concert", concertId)

		return concert.concertSchedules.map { ConcertInfo.Schedule.from(it) }
	}
}