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

	override fun findConcert(concertId: Long): Concert? {
		return concertJpaRepository.findByIdOrNull(concertId)
	}

	override fun findSchedule(concertScheduleId: Long): ConcertSchedule? {
		return concertScheduleJpaRepository.findByIdOrNull(concertScheduleId)
	}

	override fun findAllScheduleByConcertId(concertId: Long): List<ConcertSchedule> {
		return concertScheduleJpaRepository.findAllByConcertId(concertId)
	}


	override fun findSeat(concertSeatId: Long): ConcertSeat? {
		return concertSeatJpaRepository.findByIdOrNull(concertSeatId)
	}

	override fun findAllSeatByConcertScheduleId(concertScheduleId: Long): List<ConcertSeat> {
		return concertSeatJpaRepository.findAllByConcertScheduleId(concertScheduleId)
	}

	override fun save(concertSeat: ConcertSeat): ConcertSeat {
		return concertSeatJpaRepository.save(concertSeat)
	}

}