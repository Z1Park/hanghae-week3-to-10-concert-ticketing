package kr.hhplus.be.server.domain.concert

interface ConcertRepository {

	fun findAllConcert(finished: Boolean): List<Concert>

	fun findConcertWithSchedule(concertId: Long): Concert?

	fun findScheduleWithSeat(scheduleId: Long): ConcertSchedule?

	fun findConcert(concertId: Long): Concert?

	fun findSchedule(concertScheduleId: Long): ConcertSchedule?

	fun findSeat(concertSeatId: Long): ConcertSeat?
}