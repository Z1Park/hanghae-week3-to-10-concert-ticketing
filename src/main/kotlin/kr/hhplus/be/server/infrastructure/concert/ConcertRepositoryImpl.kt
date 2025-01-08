package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import org.springframework.stereotype.Repository

@Repository
class ConcertRepositoryImpl(
	private val concertJpaRepository: ConcertJpaRepository,
	private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	private val concertSeatJpaRepository: ConcertSeatJpaRepository
) : ConcertRepository {

	override fun findAllConcert(finished: Boolean): List<Concert> =
		concertJpaRepository.findAllByFinished(finished)

	override fun findConcertWithSchedule(concertId: Long): Concert? =
		concertJpaRepository.findByIdWithSchedule(concertId)

	override fun findScheduleWithSeat(scheduleId: Long): ConcertSchedule? =
		concertScheduleJpaRepository.findByIdWithSeat(scheduleId)
}