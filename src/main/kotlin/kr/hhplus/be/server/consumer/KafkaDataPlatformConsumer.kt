package kr.hhplus.be.server.consumer

import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_DATA_PLATFORM
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_RESERVATION_DATA
import kr.hhplus.be.server.domain.reservation.ReservationDataPlatformPayload
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
class KafkaDataPlatformConsumer {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 4회(1회 + 재시도 3회) attempt
	 * 데이터플랫폼 특성상 소비에 실패한 경우, 단기간에 재시도해도 해결이 안될 가능성이 높아 multiplier를 지정
	 */
	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 1000, multiplier = 3.0),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(topics = [TOPIC_SEND_RESERVATION_DATA], groupId = GROUP_DATA_PLATFORM)
	fun listenReservationSuccessMessage(@Payload payload: ReservationDataPlatformPayload, ack: Acknowledgment) {
		log.info("데이터플랫폼 - 예약 정보 수신 : payload=$payload")
		ack.acknowledge()
	}
}