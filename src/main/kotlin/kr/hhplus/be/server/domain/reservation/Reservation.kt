package kr.hhplus.be.server.domain.reservation

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "reservation")
class Reservation(
	@Column(name = "expired_at")
	var expiredAt: LocalDateTime?,

	@Column(nullable = false)
	var price: Int,

	@Column(name = "user_id", nullable = false)
	val userId: Long,

	@Column(name = "concert_id", nullable = false)
	val concertId: Long,

	@Column(name = "concert_schedule_id", nullable = false)
	val concertScheduleId: Long,

	@Column(name = "concert_seat_id", nullable = false)
	var concertSeatId: Long
) : BaseEntity() {

	fun isExpired(currentTime: LocalDateTime): Boolean {
		return expiredAt?.isBefore(currentTime) ?: false
	}

	fun soldOut() {
		expiredAt = null
	}

	fun rollbackSoldOut(expiredAt: LocalDateTime?) {
		if (this.expiredAt == null) {
			this.expiredAt = expiredAt
		}
	}
}