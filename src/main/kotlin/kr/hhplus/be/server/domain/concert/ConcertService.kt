package kr.hhplus.be.server.domain.concert

import jakarta.transaction.Transactional
import kr.hhplus.be.server.application.event.ConcertMakeSoldOutEvent
import kr.hhplus.be.server.application.event.ConcertPreoccupySuccessEvent
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.redis.DistributedLock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val RESERVATION_TIME_MINUTES = 5L

@Service
class ConcertService(
	private val concertRepository: ConcertRepository,
	private val applicationEventPublisher: ApplicationEventPublisher
) {
	companion object {
		private const val CONCERT_SEAT_KEY = "seatPreemption:"
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

	fun getConcertInfos(concertIds: List<Long>): List<ConcertInfo.ConcertDto> {
		val topConcerts = concertRepository.findAllConcertById(concertIds)
		return topConcerts.map { ConcertInfo.ConcertDto.from(it) }
	}

	@DistributedLock(prefix = CONCERT_SEAT_KEY, key = "#command.concertSeatId")
	@Transactional
	fun preoccupyConcertSeat(command: ConcertCommand.Preoccupy, traceId: String, clockHolder: ClockHolder): ConcertInfo.ReservedSeat {
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

		val originExpiredAt = concertSeat.reservedUntil
		val expiredAt = currentTime.plusMinutes(RESERVATION_TIME_MINUTES)
		concertSeat.reserveUntil(expiredAt)
		concertRepository.save(concertSeat)

		applicationEventPublisher.publishEvent(
			ConcertPreoccupySuccessEvent(
				traceId,
				concert.id,
				concertSchedule.id,
				concertSeat.id,
				expiredAt,
				originExpiredAt
			)
		)

		return ConcertInfo.ReservedSeat.of(concert, concertSchedule, concertSeat, expiredAt, originExpiredAt)
	}

	@DistributedLock(prefix = CONCERT_SEAT_KEY, key = "#concertSeatId")
	@Transactional
	fun rollbackPreoccupyConcertSeat(concertSeatId: Long, expiredAt: LocalDateTime?) {
		val concertSeat = concertRepository.findSeat(concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${concertSeatId}")

		concertSeat.rollbackReservedUntil(expiredAt)
		concertRepository.save(concertSeat)
	}

	@DistributedLock(prefix = CONCERT_SEAT_KEY, key = "#concertSeatId")
	@Transactional
	fun makeSoldOutConcertSeat(concertSeatId: Long, traceId: String) {
		val concertSeat = concertRepository.findSeat(concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${concertSeatId}")

		val originReservedUntil = concertSeat.reservedUntil
		concertSeat.soldOut()
		concertRepository.save(concertSeat)

		applicationEventPublisher.publishEvent(
			ConcertMakeSoldOutEvent(
				traceId,
				concertSeat.id,
				originReservedUntil
			)
		)
	}

	@DistributedLock(prefix = CONCERT_SEAT_KEY, key = "#concertSeatId")
	@Transactional
	fun rollbackSoldOutedConcertSeat(concertSeatId: Long, expiredAt: LocalDateTime?) {
		val concertSeat = concertRepository.findSeat(concertSeatId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "concertSeatId=${concertSeatId}")

		concertSeat.rollbackSoldOut(expiredAt)
		concertRepository.save(concertSeat)
	}
}