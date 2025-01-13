package kr.hhplus.be.server.domain.concert

interface ConcertRepository {

	fun findAllConcert(finished: Boolean): List<Concert>

	fun findConcert(concertId: Long): Concert?

	fun findSchedule(concertScheduleId: Long): ConcertSchedule?

	fun findAllScheduleByConcertId(concertId: Long): List<ConcertSchedule>

	fun findSeat(concertSeatId: Long): ConcertSeat?

	fun findAllSeatByConcertScheduleId(concertScheduleId: Long): List<ConcertSeat>
}