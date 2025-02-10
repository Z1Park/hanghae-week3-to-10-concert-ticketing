package kr.hhplus.be.server.infrastructure.payment.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.payment.model.Payment
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "payment")
class PaymentEntity(
	@Column(nullable = false)
	var price: Int,

	@Column(name = "user_id", nullable = false)
	val userId: Long,

	@Column(name = "reservation_id", nullable = false)
	val reservationId: Long,

	id: Long = 0L

) : BaseEntity(id) {

	constructor(payment: Payment) : this(
		id = payment.id,
		price = payment.price,
		userId = payment.userId,
		reservationId = payment.reservationId,
	)

	fun toDomain(): Payment = Payment(
		id = id,
		price = price,
		userId = userId,
		reservationId = reservationId,
	)
}