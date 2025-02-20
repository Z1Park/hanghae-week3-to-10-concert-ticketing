package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_CONCERT_PREOCCUPY
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_RESERVATION_DATA
import kr.hhplus.be.server.domain.reservation.ReservationDataPlatformPayload
import kr.hhplus.be.server.domain.reservation.ReservationMessageProducer
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class ReservationKafkaMessageProducer(
	private val kafkaTemplate: KafkaTemplate<String, Any>
) : ReservationMessageProducer {

	override fun sendReservationDataPlatformMessage(payload: ReservationDataPlatformPayload) {
		kafkaTemplate.send(TOPIC_SEND_RESERVATION_DATA, payload)
	}

	override fun sendRollbackPreoccupyConcertSeatMessage(traceId: String) {
		kafkaTemplate.send(TOPIC_ROLLBACK_CONCERT_PREOCCUPY, traceId)
	}
}