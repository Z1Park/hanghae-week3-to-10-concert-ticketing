package kr.hhplus.be.server.domain.reservation.model

import kr.hhplus.be.server.domain.BaseDomain
import java.time.LocalDateTime

class Reservation(
	var expiredAt: LocalDateTime?,

	var price: Int,

	val userId: Long,

	val concertId: Long,

	val concertScheduleId: Long,

	var concertSeatId: Long,

	id: Long = 0L
) : BaseDomain(id) {

	fun isExpired(currentTime: LocalDateTime): Boolean {
		return expiredAt?.isBefore(currentTime) ?: false
	}

	fun confirm() {
		expiredAt = null
	}

	fun rollbackSoldOut(expiredAt: LocalDateTime?) {
		if (this.expiredAt == null) {
			this.expiredAt = expiredAt
		}
	}
}