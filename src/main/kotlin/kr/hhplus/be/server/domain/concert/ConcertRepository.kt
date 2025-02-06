package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.domain.concert.model.Concert
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule
import kr.hhplus.be.server.domain.concert.model.ConcertSeat

interface ConcertRepository {

	fun findAllConcert(finished: Boolean): List<Concert>

	fun findAllConcertById(concertIds: List<Long>): List<Concert>

	fun findConcert(concertId: Long): Concert?

	fun findSchedule(concertScheduleId: Long): ConcertSchedule?

	fun findAllScheduleByConcertId(concertId: Long): List<ConcertSchedule>

	fun findSeat(concertSeatId: Long): ConcertSeat?

	fun findAllSeatByConcertScheduleId(concertScheduleId: Long): List<ConcertSeat>

	fun save(concertSeat: ConcertSeat): ConcertSeat
}