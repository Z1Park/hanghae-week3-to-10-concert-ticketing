package kr.hhplus.be.server.consumer

import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_CONCERT_PREOCCUPY
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_PAYMENT_DATA
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_SEND_RESERVATION_DATA
import kr.hhplus.be.server.domain.alarm.AlarmApiClient
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DeadLetterConsumer(
	private val alarmApiClient: AlarmApiClient
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@KafkaListener(
		topics = [
			TOPIC_ROLLBACK_CONCERT_PREOCCUPY + DLQ_SUFFIX,
			TOPIC_SEND_RESERVATION_DATA + DLQ_SUFFIX,
			TOPIC_SEND_PAYMENT_DATA + DLQ_SUFFIX,
		],
		groupId = KafkaGroupIdConst.GROUP_DLQ
	)
	fun consumeSendReservationDataPlatformDeadLetter(
		payload: String,
		ack: Acknowledgment,
		@Header(KafkaHeaders.GROUP_ID) groupId: String,
		@Header(KafkaHeaders.RECEIVED_TOPIC) dlt: String,
		@Header(KafkaHeaders.ORIGINAL_TOPIC) originalTopic: String?,
		@Header(KafkaHeaders.EXCEPTION_MESSAGE) exceptionMessage: String?,
		@Header(KafkaHeaders.OFFSET) offset: Long,
		@Header(KafkaHeaders.RECEIVED_TIMESTAMP) timestamp: Long
	) {
		val message = """
			Dead Letter Received
			groupId: $groupId
			Original Topic: $originalTopic
			DLT: $dlt
			Payload: $payload
			Exception: $exceptionMessage
			Offset: $offset
			Timestamp: ${Instant.ofEpochMilli(timestamp)}
		""".trimIndent()

		log.error(message)
		alarmApiClient.sendAlarm(message)
		ack.acknowledge()
	}
}