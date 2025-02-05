package kr.hhplus.be.server.domain.concert.model

import kr.hhplus.be.server.domain.BaseDomain
import java.time.LocalDateTime

class ConcertSeat(
	var seatNumber: Int,

	var price: Int,

	val concertScheduleId: Long,

	var reservedUntil: LocalDateTime?,

	id: Long = 0L
) : BaseDomain(id) {

	fun isOnConcertSchedule(concertScheduleId: Long): Boolean = this.concertScheduleId == concertScheduleId

	fun isAvailable(currentTime: LocalDateTime): Boolean = reservedUntil?.isBefore(currentTime) ?: false

	fun reserveUntil(expiredAt: LocalDateTime) {
		reservedUntil = expiredAt
	}

	fun rollbackReservedUntil(expiredAt: LocalDateTime?) {
		reservedUntil = expiredAt
	}

	fun soldOut() {
		reservedUntil = null
	}

	fun rollbackSoldOut(expiredAt: LocalDateTime?) {
		if (reservedUntil == null) {
			reservedUntil = expiredAt
		}
	}
}