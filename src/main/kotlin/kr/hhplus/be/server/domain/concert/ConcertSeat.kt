package kr.hhplus.be.server.domain.concert

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "concert_seat")
class ConcertSeat(
	@Column(name = "seat_number", nullable = false)
	var seatNumber: Int,

	@Column(nullable = false)
	var price: Int,

	@Column(name = "concert_schedule_id", nullable = false)
	val concertScheduleId: Long,

	@Column(name = "reserved_until")
	var reservedUntil: LocalDateTime?
) : BaseEntity() {

	fun isOnConcertSchedule(concertScheduleId: Long): Boolean = this.concertScheduleId == concertScheduleId

	fun isAvailable(currentTime: LocalDateTime): Boolean = reservedUntil?.isBefore(currentTime) ?: false

	fun reserveUntil(expiredAt: LocalDateTime) {
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