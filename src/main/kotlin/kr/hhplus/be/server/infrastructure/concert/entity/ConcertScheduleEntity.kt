package kr.hhplus.be.server.infrastructure.concert.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule
import kr.hhplus.be.server.infrastructure.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "concert_schedule")
class ConcertScheduleEntity(
	@Column(name = "total_seat", nullable = false)
	var totalSeat: Int,

	@Column(name = "start_at", nullable = false)
	var startAt: LocalDateTime,

	@Column(name = "end_at", nullable = false)
	var endAt: LocalDateTime,

	@Column(name = "concert_id", nullable = false)
	val concertId: Long,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(concertSchedule: ConcertSchedule) : this(
		id = concertSchedule.id,
		totalSeat = concertSchedule.totalSeat,
		startAt = concertSchedule.startAt,
		endAt = concertSchedule.endAt,
		concertId = concertSchedule.concertId
	)

	fun toDomain(): ConcertSchedule = ConcertSchedule(
		id = id,
		totalSeat = totalSeat,
		startAt = startAt,
		endAt = endAt,
		concertId = concertId
	)
}