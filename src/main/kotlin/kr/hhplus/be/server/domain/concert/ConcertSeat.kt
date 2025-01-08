package kr.hhplus.be.server.domain.concert

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "concert_seat")
class ConcertSeat(
	@Column(name = "seat_number", nullable = false)
	var seatNumber: Int,

	@Column(nullable = false)
	var price: Int,

	@Column(name = "concert_schedule_id", nullable = false)
	val concertScheduleId: Long
) : BaseEntity() {
}