package kr.hhplus.be.server.domain.payment.model

import kr.hhplus.be.server.domain.BaseDomain

class Payment(
	var price: Int,

	val userId: Long,

	val reservationId: Long,

	id: Long = 0L,

	) : BaseDomain(id) {
}