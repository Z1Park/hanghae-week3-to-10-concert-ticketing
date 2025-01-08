package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class ConcertService(
	private val concertRepository: ConcertRepository
) {

	fun getConcert(): List<ConcertInfo.Concert> {
		val concerts = concertRepository.findAllConcert(false)

		return concerts.map { ConcertInfo.Concert.from(it) }
	}

	fun getConcertSchedule(concertId: Long): List<ConcertInfo.Schedule> {
		val concert = concertRepository.findConcertWithSchedule(concertId)
			?: throw EntityNotFoundException.fromId("Concert", concertId)

		return concert.concertSchedules.map { ConcertInfo.Schedule.from(it) }
	}

	fun getConcertSeat(concertId: Long, concertScheduleId: Long): List<ConcertInfo.Seat> {
		val schedule = concertRepository.findScheduleWithSeat(concertScheduleId)
			?: throw EntityNotFoundException.fromId("ConcertSchedule", concertScheduleId)

		return schedule.concertSeats.map { ConcertInfo.Seat.of(concertId, it) }
	}
}