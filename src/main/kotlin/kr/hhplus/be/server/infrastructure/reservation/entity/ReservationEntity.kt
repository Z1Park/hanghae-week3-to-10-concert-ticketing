package kr.hhplus.be.server.infrastructure.reservation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.reservation.model.Reservation
import kr.hhplus.be.server.infrastructure.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "reservation")
class ReservationEntity(
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
	var concertSeatId: Long,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(reservation: Reservation) : this(
		id = reservation.id,
		expiredAt = reservation.expiredAt,
		price = reservation.price,
		userId = reservation.userId,
		concertId = reservation.concertId,
		concertScheduleId = reservation.concertScheduleId,
		concertSeatId = reservation.concertSeatId
	)

	fun toDomain(): Reservation = Reservation(
		id = id,
		expiredAt = expiredAt,
		price = price,
		userId = userId,
		concertId = concertId,
		concertScheduleId = concertScheduleId,
		concertSeatId = concertSeatId
	)
}