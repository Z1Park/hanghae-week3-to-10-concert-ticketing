package kr.hhplus.be.server.application.payment

class PaymentCri {

	data class Create(
		val userUUID: String,
		val tokenUUID: String,
		val reservationId: Long
	)
}