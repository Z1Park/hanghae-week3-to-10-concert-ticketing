package kr.hhplus.be.server.domain.concert

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.redis.DistributedLock
import kr.hhplus.be.server.domain.concert.model.ConcertSeat
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val RESERVATION_TIME_MINUTES = 5L

@Service
class ConcertService(
	private val concertRepository: ConcertRepository
) {
	companion object {
		private const val SEAT_PREEMPTION_KEY = "seatPreemption:"
	}

	fun getConcert(): List<ConcertInfo.ConcertDto> {
		val concerts = concertRepository.findAllConcert(false)

		return concerts.map { ConcertInfo.ConcertDto.from(it) }
	}

	fun getConcertSchedule(concertId: Long): List<ConcertInfo.Schedule> {
		val concert = concertRepository.findConcert(concertId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertId=$concertId")

		val concertSchedules = concertRepository.findAllScheduleByConcertId(concert.id)
		return concertSchedules.map { ConcertInfo.Schedule.from(it) }
	}

	fun getConcertSeat(concertId: Long, concertScheduleId: Long): List<ConcertInfo.Seat> {
		val schedule = concertRepository.findSchedule(concertScheduleId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertScheduleId=$concertScheduleId")

		val concertSeats = concertRepository.findAllSeatByConcertScheduleId(schedule.id)
		return concertSeats.map { ConcertInfo.Seat.of(concertId, it) }
	}

	@DistributedLock(prefix = SEAT_PREEMPTION_KEY, key = "#command.concertSeatId")
	@Transactional
	fun preoccupyConcertSeat(command: ConcertCommand.Reserve, clockHolder: ClockHolder): ConcertInfo.ReservedSeat {
		val concertSeat = concertRepository.findSeat(command.concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${command.concertSeatId}")
		val concertSchedule = concertRepository.findSchedule(command.concertScheduleId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertScheduleId=${command.concertScheduleId}")
		val concert = concertRepository.findConcert(command.concertId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertId=${command.concertId}")

		require(concertSchedule.isOnConcert(concert.id)) {
			throw CustomException(ErrorCode.NOT_MATCH_SCHEDULE, "concertId=${concert.id}, concertScheduleId=${concertSchedule.id}")
		}
		require(concertSeat.isOnConcertSchedule(concertSchedule.id)) {
			throw CustomException(ErrorCode.NOT_MATCH_SEAT, "concertScheduleId=${concertSchedule.id}, concertSeatId=${concertSeat.id}")
		}

		val currentTime = clockHolder.getCurrentTime()
		require(concertSeat.isAvailable(currentTime)) {
			throw CustomException(ErrorCode.ALREADY_RESERVED, "concertSeatId=$concertSeat.id")
		}

		val expiredAt = currentTime.plusMinutes(RESERVATION_TIME_MINUTES)
		concertSeat.reserveUntil(expiredAt)
		concertRepository.save(concertSeat)

		return ConcertInfo.ReservedSeat.of(concertSeat, expiredAt)
	}

	@Transactional
	fun makeSoldOutConcertSeat(concertSeatId: Long): ConcertSeat {
		val concertSeat = concertRepository.findSeat(concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${concertSeatId}")

		concertSeat.soldOut()
		return concertRepository.save(concertSeat)
	}

	@Transactional
	fun rollbackSoldOutedConcertSeat(concertSeatId: Long, expiredAt: LocalDateTime?) {
		val concertSeat = concertRepository.findSeat(concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${concertSeatId}")

		concertSeat.rollbackSoldOut(expiredAt)
		concertRepository.save(concertSeat)
	}
}