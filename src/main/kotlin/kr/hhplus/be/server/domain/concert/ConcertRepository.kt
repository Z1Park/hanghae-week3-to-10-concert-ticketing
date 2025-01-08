package kr.hhplus.be.server.domain.concert

interface ConcertRepository {

	fun findAllConcert(finished: Boolean): List<Concert>

	fun findConcertWithSchedule(concertId: Long): Concert?

	fun findScheduleWithSeat(scheduleId: Long): ConcertSchedule?
}