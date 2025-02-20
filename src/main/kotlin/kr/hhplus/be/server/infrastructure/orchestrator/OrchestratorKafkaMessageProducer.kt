package kr.hhplus.be.server.infrastructure.orchestrator

import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_PAY_RESERVATION
import kr.hhplus.be.server.domain.orchestrator.OrchestratorMessageProducer
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrchestratorKafkaMessageProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) : OrchestratorMessageProducer {

	override fun sendRollbackPayReservationMessage(traceId: String) {
		kafkaTemplate.send(TOPIC_ROLLBACK_PAY_RESERVATION, traceId)
	}
}