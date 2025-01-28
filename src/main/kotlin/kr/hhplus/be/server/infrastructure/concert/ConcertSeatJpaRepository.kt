package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertSeatJpaRepository : JpaRepository<ConcertSeatEntity, Long> {

	fun findAllByConcertScheduleId(concertScheduleId: Long): List<ConcertSeatEntity>
}