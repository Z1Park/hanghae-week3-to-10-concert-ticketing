package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.model.Concert
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule
import kr.hhplus.be.server.domain.concert.model.ConcertSeat
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ConcertRepositoryImpl(
	private val concertJpaRepository: ConcertJpaRepository,
	private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	private val concertSeatJpaRepository: ConcertSeatJpaRepository
) : ConcertRepository {

	override fun findAllConcert(finished: Boolean): List<Concert> =
		concertJpaRepository.findAllByFinished(finished).map { it.toDomain() }

	override fun findAllConcertById(concertIds: List<Long>): List<Concert> =
		concertJpaRepository.findAllByIdIn(concertIds).map { it.toDomain() }

	override fun findConcert(concertId: Long): Concert? =
		concertJpaRepository.findByIdOrNull(concertId)?.toDomain()

	override fun findSchedule(concertScheduleId: Long): ConcertSchedule? =
		concertScheduleJpaRepository.findByIdOrNull(concertScheduleId)?.toDomain()

	override fun findAllScheduleByConcertId(concertId: Long): List<ConcertSchedule> =
		concertScheduleJpaRepository.findAllByConcertId(concertId).map { it.toDomain() }

	override fun findSeat(concertSeatId: Long): ConcertSeat? =
		concertSeatJpaRepository.findByIdOrNull(concertSeatId)?.toDomain()

	override fun findAllSeatByConcertScheduleId(concertScheduleId: Long): List<ConcertSeat> =
		concertSeatJpaRepository.findAllByConcertScheduleId(concertScheduleId).map { it.toDomain() }

	override fun save(concertSeat: ConcertSeat): ConcertSeat =
		concertSeatJpaRepository.save(ConcertSeatEntity(concertSeat)).toDomain()
}