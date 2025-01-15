package kr.hhplus.be.server.domain.reservation

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class ReservationService(
	private val reservationRepository: ReservationRepository
) {

	fun getReservationForPay(userId: Long, reservationId: Long, clockHolder: ClockHolder): Reservation {
		val reservation = reservationRepository.findByUserIdAndReservationId(userId, reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "userId=$userId, reservationId=$reservationId")

		require(!reservation.isExpired(clockHolder.getCurrentTime())) {
			throw CustomException(ErrorCode.EXPIRED_RESERVATION)
		}

		return reservation
	}

	@Transactional
	fun reserve(command: ReservationCommand.Create, clockHolder: ClockHolder): Reservation {
		val seatReservation = reservationRepository.findByScheduleAndSeatForUpdate(command.concertScheduleId, command.concertSeatId)
		val currentTime = clockHolder.getCurrentTime()
		require(seatReservation == null || seatReservation.isExpired(currentTime)) {
			throw CustomException(ErrorCode.ALREADY_RESERVED)
		}

		val expiredAt = clockHolder.getCurrentTime().plusMinutes(5)
		return reservationRepository.save(command.toReservation(expiredAt))
	}

	@Transactional
	fun makeSoldOut(reservation: Reservation) {
		reservation.soldOut()
		reservationRepository.save(reservation)
	}
}