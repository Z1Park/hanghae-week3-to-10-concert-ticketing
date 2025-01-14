package kr.hhplus.be.server.domain.reservation

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.exception.AlreadyReservedException
import kr.hhplus.be.server.domain.exception.ReservationExpiredException
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class ReservationService(
	private val reservationRepository: ReservationRepository
) {

	fun getReservationForPay(userId: Long, reservationId: Long, clockHolder: ClockHolder): Reservation {
		val reservation = reservationRepository.findByUserIdAndReservationId(userId, reservationId)
			?: throw BadRequestException()

		require(!reservation.isExpired(clockHolder.getCurrentTime())) { throw ReservationExpiredException() }

		return reservation
	}

	@Transactional
	fun reserve(request: ReservationCommand.Create, clockHolder: ClockHolder): Reservation {
		val seatReservation = reservationRepository.findByScheduleAndSeatForUpdate(request.concertScheduleId, request.concertSeatId)
		val currentTime = clockHolder.getCurrentTime()
		require(seatReservation == null || seatReservation.isExpired(currentTime)) { throw AlreadyReservedException() }

		val expiredAt = clockHolder.getCurrentTime().plusMinutes(5)
		return reservationRepository.save(request.toReservation(expiredAt))
	}

	@Transactional
	fun makeSoldOut(reservation: Reservation) {
		reservation.soldOut()
		reservationRepository.save(reservation)
	}
}