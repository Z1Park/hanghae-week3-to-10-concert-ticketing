package kr.hhplus.be.server.domain.payment

class PaymentCommand {

	data class Create(
		val price: Int,
		val userId: Long,
		val reservationId: Long
	) {
		fun toPayment(): Payment = Payment(price, userId, reservationId)
	}
}