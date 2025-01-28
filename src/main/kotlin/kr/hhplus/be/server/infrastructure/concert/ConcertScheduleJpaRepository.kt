package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.infrastructure.concert.entity.ConcertScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertScheduleJpaRepository : JpaRepository<ConcertScheduleEntity, Long> {

	fun findAllByConcertId(concertId: Long): List<ConcertScheduleEntity>
}