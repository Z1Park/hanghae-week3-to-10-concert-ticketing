package kr.hhplus.be.server.common.kafka

/**
 * Topic을 코드로 관리하는 경우, topic 추가가 필요할 때 재배포가 필요
 * 하지만 학습 과정 + 관리의 편의성을 위해 코드로 구현
 * (사실 topic이 추가되면 consumer, producer도 다시 구성해야하니 재배포가 정상수순이 아닐까...)
 */
class KafkaTopicNameConst {

	companion object {
		const val TOPIC_ROLLBACK_CONCERT_PREOCCUPY = "kafka.message.concert.rollback.preoccupy"
		const val TOPIC_SEND_RESERVATION_DATA = "kafka.message.reservation.send.data"

		const val DLQ_SUFFIX = ".dlq"

		// Topic에 따른 partition 수를 관리
		val topics = mapOf(
			Pair(TOPIC_ROLLBACK_CONCERT_PREOCCUPY, 3),
			Pair(TOPIC_SEND_RESERVATION_DATA, 3)
		)
	}
}