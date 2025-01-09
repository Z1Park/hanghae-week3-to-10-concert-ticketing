package kr.hhplus.be.server.interfaces.payment

import kr.hhplus.be.server.application.payment.PaymentCri

data class PayRequest(
	val reservationId: Long
) {
	fun toPaymentCriCreate(userUUID: String, tokenUUID: String): PaymentCri.Create =
		PaymentCri.Create(userUUID, tokenUUID, reservationId)
}
