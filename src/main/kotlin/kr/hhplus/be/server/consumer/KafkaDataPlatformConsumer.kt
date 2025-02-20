package kr.hhplus.be.server.consumer

import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_DATA_PLATFORM
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_PAYMENT_DATA
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_RESERVATION_DATA
import kr.hhplus.be.server.domain.payment.PaymentPayload
import kr.hhplus.be.server.domain.reservation.ReservationDataPlatformPayload
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaDataPlatformConsumer {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 데이터플랫폼 전송은 실패하더라도 시간이 지나면 처리될 가능성이 높기 때문에 DLQ가 아닌 스케줄러를 통해 처리
	 */
	@KafkaListener(topics = [TOPIC_SEND_RESERVATION_DATA], groupId = GROUP_DATA_PLATFORM)
	fun listenReservationSuccessMessage(@Payload payload: ReservationDataPlatformPayload, ack: Acknowledgment) {
		log.info("데이터플랫폼 - 예약 정보 수신 : payload=$payload")
		ack.acknowledge()
	}

	@KafkaListener(topics = [TOPIC_SEND_PAYMENT_DATA], groupId = GROUP_DATA_PLATFORM)
	fun listenPaymentSuccessMessage(@Payload payload: PaymentPayload, ack: Acknowledgment) {
		log.info("데이터플랫폼 - 결제 정보 수신 : payload=$payload")
		ack.acknowledge()
	}
}