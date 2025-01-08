package kr.hhplus.be.server.domain.concert

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "concert_seat")
class ConcertSeat(
	@Column(name = "seat_number", nullable = false)
	var seatNumber: Int,

	@Column(nullable = false)
	var price: Int,

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "concert_schedule_id", nullable = false)
	val concertSchedule: ConcertSchedule
) : BaseEntity() {
}