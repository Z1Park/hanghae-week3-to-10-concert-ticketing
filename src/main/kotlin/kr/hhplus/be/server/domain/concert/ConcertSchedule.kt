package kr.hhplus.be.server.domain.concert

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
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
) : BaseEntity() {

	fun isOnConcert(concertId: Long): Boolean =
		this.concertId == concertId
}