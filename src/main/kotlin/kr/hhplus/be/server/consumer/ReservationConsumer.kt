package kr.hhplus.be.server.consumer

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_RESERVATION
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_PAY_RESERVATION
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_RESERVATION_DATA
import kr.hhplus.be.server.domain.reservation.ReservationDataPlatformPayload
import kr.hhplus.be.server.domain.reservation.ReservationOutboxService
import kr.hhplus.be.server.domain.reservation.ReservationService
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
class ReservationConsumer(
	private val reservationOutboxService: ReservationOutboxService,
	private val reservationService: ReservationService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 100, multiplier = 2.0),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(
		topics = [TOPIC_SEND_RESERVATION_DATA],
		groupId = GROUP_RESERVATION,
	)
	fun listenReservationSuccessMessage(@Payload payload: ReservationDataPlatformPayload, ack: Acknowledgment) {
		log.debug("데이터플랫폼에 예약 정보 전송 시 메세지 발신 성공 확인을 위한 셀프 consume")

		reservationOutboxService.processReservationDataPlatformMessage(payload.traceId)
		ack.acknowledge()
	}

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 100),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(
		topics = [TOPIC_ROLLBACK_PAY_RESERVATION],
		groupId = GROUP_RESERVATION
	)
	@Transactional
	fun listenRollbackReservationConfirm(@Payload traceId: String, ack: Acknowledgment) {
		log.warn("예약 확정 처리 롤백 시퀀스 수행 : traceId=$traceId")

		val payload = reservationOutboxService.processRollbackReservationConfirm(traceId)
		reservationService.rollbackReservation(payload.reservationId, payload.originExpiredAt)

		ack.acknowledge()
	}
}