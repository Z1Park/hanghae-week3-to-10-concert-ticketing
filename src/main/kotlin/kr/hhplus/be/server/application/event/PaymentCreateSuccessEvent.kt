package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.payment.PaymentCreatePayload

data class PaymentCreateSuccessEvent(
	val traceId: String,
	val paymentId: Long,
	val reservationId: Long,
	val userId: Long,
) {

	fun toPaymentCreatePayload(): PaymentCreatePayload =
		PaymentCreatePayload(traceId, paymentId, reservationId, userId)
}
