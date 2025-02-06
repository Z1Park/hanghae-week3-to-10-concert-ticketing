package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertJpaRepository : JpaRepository<ConcertEntity, Long> {

	fun findAllByFinished(finished: Boolean): List<ConcertEntity>

	fun findAllByIdIn(ids: List<Long>): List<ConcertEntity>
}