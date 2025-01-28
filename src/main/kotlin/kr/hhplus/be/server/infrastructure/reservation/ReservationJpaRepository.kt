package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationJpaRepository : JpaRepository<ReservationEntity, Long> {

	fun findByConcertScheduleIdAndConcertSeatId(concertScheduleId: Long, concertSeatId: Long): ReservationEntity?
}