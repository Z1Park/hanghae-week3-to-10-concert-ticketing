package kr.hhplus.be.server.domain.reservation

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.exception.AlreadyReservedException
import org.springframework.stereotype.Service

@Service
class ReservationService(
	private val reservationRepository: ReservationRepository
) {

	@Transactional
	fun reserve(request: ReservationCommand.Create, clockHolder: ClockHolder): Reservation {
		val seatReservation = reservationRepository.findByScheduleAndSeatForUpdate(request.concertScheduleId, request.concertSeatId)
		val currentTime = clockHolder.getCurrentTime()
		require(seatReservation == null || seatReservation.isExpired(currentTime)) { throw AlreadyReservedException() }

		val expiredAt = clockHolder.getCurrentTime().plusMinutes(5)
		return reservationRepository.save(request.toReservation(expiredAt))
	}
}