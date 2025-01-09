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
		val concert = concertRepository.findConcertWithSchedule(concertId)
			?: throw EntityNotFoundException.fromId("Concert", concertId)

		return concert.concertSchedules.map { ConcertInfo.Schedule.from(it) }
	}

	fun getConcertSeat(concertId: Long, concertScheduleId: Long): List<ConcertInfo.Seat> {
		val schedule = concertRepository.findScheduleWithSeat(concertScheduleId)
			?: throw EntityNotFoundException.fromId("ConcertSchedule", concertScheduleId)

		return schedule.concertSeats.map { ConcertInfo.Seat.of(concertId, it) }
	}

	fun getConcertSeatTotalInformation(command: ConcertCommand.Total): ConcertInfo.Total {
		val concert = concertRepository.findConcert(command.concertId)
			?: throw EntityNotFoundException.fromId("Concert", command.concertId)
		val concertSchedule = concertRepository.findSchedule(command.concertScheduleId)
			?: throw EntityNotFoundException.fromId("ConcertSchedule", command.concertScheduleId)
		val concertSeat = concertRepository.findSeat(command.concertSeatId)
			?: throw EntityNotFoundException.fromId("ConcertSeat", command.concertSeatId)

		println(concertSchedule.isOnConcert(concert.id))
		println(concertSeat.isOnConcertSchedule(concertSchedule.id))
		require(concertSchedule.isOnConcert(concert.id)) { throw BadRequestException() }
		require(concertSeat.isOnConcertSchedule(concertSchedule.id)) { throw BadRequestException() }

		return ConcertInfo.Total.of(concert, concertSchedule, concertSeat)
	}
}