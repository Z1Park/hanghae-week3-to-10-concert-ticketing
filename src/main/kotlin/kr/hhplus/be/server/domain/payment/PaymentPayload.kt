package kr.hhplus.be.server.domain.payment

data class PaymentPayload(
	val traceId: String,
	val paymentId: Long,
	val reservationId: Long?,
	val userId: Long?,
	val price: Int?
)
