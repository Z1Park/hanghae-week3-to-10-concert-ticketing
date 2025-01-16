package kr.hhplus.be.server.infrastructure.reservation

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.reservation.Reservation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface ReservationJpaRepository : JpaRepository<Reservation, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	fun findForUpdateByConcertScheduleIdAndConcertSeatId(concertScheduleId: Long, concertSeatId: Long): Reservation?
}