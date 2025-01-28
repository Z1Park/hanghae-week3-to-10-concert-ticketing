package kr.hhplus.be.server.infrastructure.concert.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.concert.model.ConcertSeat
import kr.hhplus.be.server.infrastructure.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "concert_seat")
class ConcertSeatEntity(
	@Column(name = "seat_number", nullable = false)
	var seatNumber: Int,

	@Column(nullable = false)
	var price: Int,

	@Column(name = "concert_schedule_id", nullable = false)
	val concertScheduleId: Long,

	@Column(name = "reserved_until")
	var reservedUntil: LocalDateTime?,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(concertSeat: ConcertSeat) : this(
		id = concertSeat.id,
		seatNumber = concertSeat.seatNumber,
		price = concertSeat.price,
		concertScheduleId = concertSeat.concertScheduleId,
		reservedUntil = concertSeat.reservedUntil
	)

	fun toDomain(): ConcertSeat = ConcertSeat(
		id = id,
		seatNumber = seatNumber,
		price = price,
		concertScheduleId = concertScheduleId,
		reservedUntil = reservedUntil
	)
}