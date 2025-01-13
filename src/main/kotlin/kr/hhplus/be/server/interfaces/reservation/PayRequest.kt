package kr.hhplus.be.server.interfaces.reservation

import kr.hhplus.be.server.application.reservation.PaymentCri

data class PayRequest(
	val reservationId: Long
) {
	fun toPaymentCriCreate(userUUID: String, tokenUUID: String): PaymentCri.Create =
		PaymentCri.Create(userUUID, tokenUUID, reservationId)
}
