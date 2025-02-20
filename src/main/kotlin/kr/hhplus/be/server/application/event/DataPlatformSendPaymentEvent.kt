package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.payment.PaymentPayload

data class DataPlatformSendPaymentEvent(
	val traceId: String,
	val paymentId: Long,
	val reservationId: Long,
	val userId: Long,
	val price: Int
) {

	fun toPaymentPayload(): PaymentPayload =
		PaymentPayload(traceId, paymentId, reservationId, userId, price)
}