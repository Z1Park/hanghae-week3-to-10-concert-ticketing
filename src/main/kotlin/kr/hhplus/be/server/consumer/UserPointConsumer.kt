package kr.hhplus.be.server.consumer

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_USER
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.DLQ_SUFFIX
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst.Companion.TOPIC_ROLLBACK_PAY_RESERVATION
import kr.hhplus.be.server.domain.user.UserPointOutboxService
import kr.hhplus.be.server.domain.user.UserService
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
class UserPointConsumer(
	private val userPointOutboxService: UserPointOutboxService,
	private val userService: UserService
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
		topics = [TOPIC_ROLLBACK_PAY_RESERVATION],
		groupId = GROUP_USER
	)
	@Transactional
	fun listenRollbackPointUse(@Payload traceId: String, ack: Acknowledgment) {
		log.warn("유저 포인트 차감 롤백 시퀀스 수행 : traceId=$traceId")

		val payload = userPointOutboxService.processRollbackUsePoint(traceId)
		userService.rollbackUsePointHistory(payload.userId, payload.pointHistoryId!!)

		ack.acknowledge()
	}
}