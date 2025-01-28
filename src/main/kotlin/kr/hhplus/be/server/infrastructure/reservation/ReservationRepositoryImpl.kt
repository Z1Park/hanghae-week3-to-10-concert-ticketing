package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.domain.reservation.ReservationRepository
import kr.hhplus.be.server.domain.reservation.model.Reservation
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ReservationRepositoryImpl(
	private val reservationJpaRepository: ReservationJpaRepository
) : ReservationRepository {

	override fun findById(reservationId: Long): Reservation? =
		reservationJpaRepository.findByIdOrNull(reservationId)?.toDomain()

	override fun findByScheduleIdAndSeatId(concertScheduleId: Long, concertSeatId: Long): Reservation? =
		reservationJpaRepository.findByConcertScheduleIdAndConcertSeatId(concertScheduleId, concertSeatId)?.toDomain()

	override fun save(reservation: Reservation): Reservation =
		reservationJpaRepository.save(ReservationEntity(reservation)).toDomain()

	override fun delete(reservation: Reservation) {
		reservationJpaRepository.delete(ReservationEntity(reservation))
	}
}