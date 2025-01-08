package kr.hhplus.be.server.domain.concert

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "concert_schedule")
class ConcertSchedule(
	@Column(name = "total_seat", nullable = false)
	var totalSeat: Int,

	@Column(name = "start_at", nullable = false)
	var startAt: LocalDateTime,

	@Column(name = "end_at", nullable = false)
	var endAt: LocalDateTime,

	@Column(name = "concert_id", nullable = false)
	val concertId: Long,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concertSchedule")
	val concertSeats: List<ConcertSeat> = mutableListOf()
) : BaseEntity() {
}