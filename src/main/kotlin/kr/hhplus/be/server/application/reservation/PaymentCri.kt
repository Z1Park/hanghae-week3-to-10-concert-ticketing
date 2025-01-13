package kr.hhplus.be.server.application.reservation

class PaymentCri {

	data class Create(
		val userUUID: String,
		val tokenUUID: String,
		val reservationId: Long
	)
}