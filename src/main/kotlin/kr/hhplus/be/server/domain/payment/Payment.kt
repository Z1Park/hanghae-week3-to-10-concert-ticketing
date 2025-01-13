package kr.hhplus.be.server.domain.payment

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "payment")
class Payment(
	@Column(nullable = false)
	var price: Int,

	@Column(name = "user_id", nullable = false)
	val userId: Long,

	@Column(name = "reservation_id", nullable = false)
	val reservationId: Long
) : BaseEntity() {
}