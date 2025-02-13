package kr.hhplus.be.server.domain.reservation

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.redis.DistributedLock
import kr.hhplus.be.server.domain.reservation.model.Reservation
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReservationService(
	private val reservationRepository: ReservationRepository
) {
	companion object {
		private const val RESERVATION_KEY = "reservation:"
		private const val CACHE_SIZE = 20L
	}

	fun getReservationForPay(reservationId: Long, clockHolder: ClockHolder): Reservation {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		if (reservation.isExpired(clockHolder.getCurrentTime())) {
			throw CustomException(ErrorCode.EXPIRED_RESERVATION)
		}

		return reservation
	}

	@Transactional
	fun reserve(command: ReservationCommand.Create, clockHolder: ClockHolder): Reservation {
		val seatReservation = reservationRepository.findByScheduleIdAndSeatId(command.concertScheduleId, command.concertSeatId)

		if (seatReservation != null) {
			val currentTime = clockHolder.getCurrentTime()
			require(seatReservation.isExpired(currentTime)) { throw CustomException(ErrorCode.ALREADY_RESERVED) }

			reservationRepository.delete(seatReservation)
		}

		val reservation = command.toReservation()
		return reservationRepository.save(reservation)
	}

	@DistributedLock(prefix = RESERVATION_KEY, key = "#reservationId")
	@Transactional
	fun makeSoldOut(reservationId: Long) {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		reservation.soldOut()
		reservationRepository.save(reservation)
	}

	@Transactional
	fun rollbackReservation(reservationId: Long, expiredAt: LocalDateTime?) {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		reservation.rollbackSoldOut(expiredAt)
		reservationRepository.save(reservation)
	}

	fun getYesterdayReservationConcertCounts(clockHolder: ClockHolder): List<Long> {
		val end = clockHolder.getCurrentTime().toLocalDate().atStartOfDay()
		val start = end.minusDays(1)

		return reservationRepository.findTopReservationConcertIdsByCreatedAtBetween(start, end, CACHE_SIZE)
	}
}