package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class ConcertService(
	private val concertRepository: ConcertRepository
) {

	fun getConcert(): List<ConcertInfo.ConcertDto> {
		val concerts = concertRepository.findAllConcert(false)

		return concerts.map { ConcertInfo.ConcertDto.from(it) }
	}

	fun getConcertSchedule(concertId: Long): List<ConcertInfo.Schedule> {
		val concert = concertRepository.findConcert(concertId)
			?: throw EntityNotFoundException.fromId("Concert", concertId)

		val concertSchedules = concertRepository.findAllScheduleByConcertId(concert.id)
		return concertSchedules.map { ConcertInfo.Schedule.from(it) }
	}

	fun getConcertSeat(concertId: Long, concertScheduleId: Long): List<ConcertInfo.Seat> {
		val schedule = concertRepository.findSchedule(concertScheduleId)
			?: throw EntityNotFoundException.fromId("ConcertSchedule", concertScheduleId)

		val concertSeats = concertRepository.findAllSeatByConcertScheduleId(schedule.id)
		return concertSeats.map { ConcertInfo.Seat.of(concertId, it) }
	}

	fun getConcertSeatDetailInformation(command: ConcertCommand.Total): ConcertInfo.Detail {
		val concert = concertRepository.findConcert(command.concertId)
			?: throw EntityNotFoundException.fromId("Concert", command.concertId)
		val concertSchedule = concertRepository.findSchedule(command.concertScheduleId)
			?: throw EntityNotFoundException.fromId("ConcertSchedule", command.concertScheduleId)
		val concertSeat = concertRepository.findSeat(command.concertSeatId)
			?: throw EntityNotFoundException.fromId("ConcertSeat", command.concertSeatId)

		require(concertSchedule.isOnConcert(concert.id)) { throw BadRequestException() }
		require(concertSeat.isOnConcertSchedule(concertSchedule.id)) { throw BadRequestException() }

		return ConcertInfo.Detail.of(concert, concertSchedule, concertSeat)
	}
}