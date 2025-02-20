package kr.hhplus.be.server.consumer

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_PAYMENT
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_PAY_RESERVATION
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_PAYMENT_DATA
import kr.hhplus.be.server.domain.payment.PaymentOutboxService
import kr.hhplus.be.server.domain.payment.PaymentPayload
import kr.hhplus.be.server.domain.payment.PaymentService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class PaymentConsumer(
	private val paymentOutboxService: PaymentOutboxService,
	private val paymentService: PaymentService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 100, multiplier = 2.0),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(topics = [TOPIC_SEND_PAYMENT_DATA], groupId = GROUP_PAYMENT)
	fun listenPaymentSuccessMessage(@Payload payload: PaymentPayload, ack: Acknowledgment) {
		log.debug("데이터플랫폼에 결제 정보 전송 시 메세지 발신 성공 확인을 위한 셀프 consume")

		paymentOutboxService.processPaymentDataPlatformMessage(payload.traceId)
		ack.acknowledge()
	}

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 100, multiplier = 2.0),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(topics = [TOPIC_ROLLBACK_PAY_RESERVATION], groupId = GROUP_PAYMENT)
	@Transactional
	fun listenRollbackPayReservationMessage(@Payload traceId: String, ack: Acknowledgment) {
		log.debug("결제 생성 롤백 시퀀스 실행 : traceId=$traceId")

		val payload = paymentOutboxService.processRollbackPayReservation(traceId)
		paymentService.rollbackPayment(payload.paymentId)

		ack.acknowledge()
	}
}