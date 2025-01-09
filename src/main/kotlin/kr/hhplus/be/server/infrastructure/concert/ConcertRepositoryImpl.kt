package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertRepository
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import kr.hhplus.be.server.domain.concert.ConcertSeat
import org.springframework.data.repository.findByIdOrNull
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

	override fun findConcert(concertId: Long): Concert? {
		return concertJpaRepository.findByIdOrNull(concertId)
	}

	override fun findSchedule(concertScheduleId: Long): ConcertSchedule? {
		return concertScheduleJpaRepository.findByIdOrNull(concertScheduleId)
	}

	override fun findSeat(concertSeatId: Long): ConcertSeat? {
		return concertSeatJpaRepository.findByIdOrNull(concertSeatId)
	}
}