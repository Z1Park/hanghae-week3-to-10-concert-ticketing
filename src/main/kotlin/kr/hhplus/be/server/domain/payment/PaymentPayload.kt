package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.infrastructure.payment.entity.PaymentOutboxMessage

data class PaymentPayload(
	val traceId: String,
	val paymentId: Long,
	val reservationId: Long?,
	val userId: Long?,
	val price: Int?
) {
	companion object {
		fun from(outboxMessage: PaymentOutboxMessage): PaymentPayload =
			PaymentPayload(
				outboxMessage.traceId,
				outboxMessage.paymentId,
				outboxMessage.reservationId,
				outboxMessage.userId,
				outboxMessage.price
			)
	}
}
