package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.ConcertSchedule
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConcertScheduleJpaRepository : JpaRepository<ConcertSchedule, Long> {

	@Query(
		"""
		select cs from ConcertSchedule cs
		left join fetch cs.concertSeats
		where cs.id = :id
	"""
	)
	fun findByIdWithSeat(@Param("id") id: Long): ConcertSchedule?
}