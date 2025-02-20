package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_PAYMENT_DATA
import kr.hhplus.be.server.domain.payment.PaymentMessageProducer
import kr.hhplus.be.server.domain.payment.PaymentPayload
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentKafkaMessageProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) : PaymentMessageProducer {

	override fun sendPaymentDataPlatformMessage(payload: PaymentPayload) {
		kafkaTemplate.send(TOPIC_SEND_PAYMENT_DATA, payload)
	}
}