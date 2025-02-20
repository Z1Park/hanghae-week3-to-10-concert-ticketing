package kr.hhplus.be.server.consumer

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_CONCERT
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_CONCERT_PREOCCUPY
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_PAY_RESERVATION
import kr.hhplus.be.server.domain.concert.ConcertOutboxService
import kr.hhplus.be.server.domain.concert.ConcertService
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
class ConcertConsumer(
	private val concertOutboxService: ConcertOutboxService,
	private val concertService: ConcertService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@RetryableTopic(
		attempts = "4",
		backoff = Backoff(delay = 100),
		topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
		dltTopicSuffix = DLQ_SUFFIX,
		dltStrategy = DltStrategy.FAIL_ON_ERROR
	)
	@KafkaListener(
		topics = [TOPIC_ROLLBACK_CONCERT_PREOCCUPY],
		groupId = GROUP_CONCERT
	)
	@Transactional
	fun listenRollbackConcertPreoccupyMessage(@Payload traceId: String, ack: Acknowledgment) {
		log.warn("좌석 선점 롤백 시퀀스 수행 : traceId=$traceId")

		val payload = concertOutboxService.processRollbackConcertPreoccupy(traceId)
		concertService.rollbackPreoccupyConcertSeat(payload.concertSeatId, payload.originExpiredAt)

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
		groupId = GROUP_CONCERT
	)
	@Transactional
	fun listenRollbackConcertSeatSoldOut(@Payload traceId: String, ack: Acknowledgment) {
		log.warn("좌석 매진 처리 롤백 시퀀스 수행 : traceId=$traceId")

		val payload = concertOutboxService.processRollbackConcertSeatSoldOut(traceId)
		concertService.rollbackSoldOutedConcertSeat(payload.concertSeatId, payload.originExpiredAt)

		ack.acknowledge()
	}
}