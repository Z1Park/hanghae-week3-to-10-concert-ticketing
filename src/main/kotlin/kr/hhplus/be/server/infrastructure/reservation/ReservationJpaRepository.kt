package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.domain.reservation.Reservation
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationJpaRepository : JpaRepository<Reservation, Long> {

	fun findByConcertScheduleIdAndConcertSeatId(concertScheduleId: Long, concertSeatId: Long): Reservation?
}