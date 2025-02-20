package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.PaymentFailEvent
import kr.hhplus.be.server.domain.orchestrator.OrchestratorMessageProducer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrchestratorEventListener(
	private val orchestratorMessageProducer: OrchestratorMessageProducer
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handlePaymentFailEvent(event: PaymentFailEvent) {
		log.warn("결제 실패 - 롤백 요청 메세지 발행 : event=$event")

		orchestratorMessageProducer.sendRollbackPayReservationMessage(event.traceId)
	}
}