package kr.hhplus.be.server.domain.payment

data class PaymentCreatePayload(
	val traceId: String,
	val paymentId: Long,
	val reservationId: Long?,
	val userId: Long?
)
