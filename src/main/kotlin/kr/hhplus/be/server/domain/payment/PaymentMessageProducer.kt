package kr.hhplus.be.server.domain.payment

interface PaymentMessageProducer {

	fun sendPaymentDataPlatformMessage(payload: PaymentPayload)
}