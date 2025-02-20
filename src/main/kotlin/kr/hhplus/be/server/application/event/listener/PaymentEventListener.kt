package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.DataPlatformSendPaymentEvent
import kr.hhplus.be.server.domain.payment.PaymentMessageProducer
import kr.hhplus.be.server.domain.payment.PaymentOutboxService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
	private val paymentOutboxService: PaymentOutboxService,
	private val paymentMessageProducer: PaymentMessageProducer
) {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 결제 완료 이후 데이터 플랫폼으로 결제 내역 전송
	 */
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun savePaymentInfoToOutbox(event: DataPlatformSendPaymentEvent) {
		log.debug("결제 정보 outbox 저장: event=$event")

		paymentOutboxService.savePaymentInfo(event.toPaymentPayload())
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handlePaymentSuccessEvent(event: DataPlatformSendPaymentEvent) {
		log.debug("결제 성공 - 데이터 플랫폼 전송 : event=$event")

		paymentMessageProducer.sendPaymentDataPlatformMessage(event.toPaymentPayload())
	}
}