package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.Concert
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertJpaRepository : JpaRepository<Concert, Long> {

	fun findAllByFinished(finished: Boolean): List<Concert>

	@Query(
		"""
		select c from Concert c
		left join fetch c.concertSchedules
		where c.id = :id
	"""
	)
	fun findByIdWithSchedule(@Param("id") id: Long): Concert?
}