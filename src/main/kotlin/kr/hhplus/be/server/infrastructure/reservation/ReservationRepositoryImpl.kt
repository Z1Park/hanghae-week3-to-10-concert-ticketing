package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.domain.reservation.Reservation
import kr.hhplus.be.server.domain.reservation.ReservationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ReservationRepositoryImpl(
	private val reservationJpaRepository: ReservationJpaRepository
) : ReservationRepository {

	override fun findById(reservationId: Long): Reservation? =
		reservationJpaRepository.findByIdOrNull(reservationId)

	override fun findByScheduleIdAndSeatId(concertScheduleId: Long, concertSeatId: Long): Reservation? =
		reservationJpaRepository.findByConcertScheduleIdAndConcertSeatId(concertScheduleId, concertSeatId)

	override fun save(reservation: Reservation): Reservation =
		reservationJpaRepository.save(reservation)

	override fun delete(reservation: Reservation) {
		reservationJpaRepository.delete(reservation)
	}
}