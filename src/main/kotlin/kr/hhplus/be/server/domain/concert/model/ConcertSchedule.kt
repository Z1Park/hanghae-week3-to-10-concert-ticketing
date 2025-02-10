package kr.hhplus.be.server.domain.concert.model

import kr.hhplus.be.server.domain.BaseDomain
import java.time.LocalDateTime

class ConcertSchedule(
	var totalSeat: Int,

	var startAt: LocalDateTime,

	var endAt: LocalDateTime,

	val concertId: Long,

	id: Long = 0L
) : BaseDomain(id) {

	fun isOnConcert(concertId: Long): Boolean =
		this.concertId == concertId
}