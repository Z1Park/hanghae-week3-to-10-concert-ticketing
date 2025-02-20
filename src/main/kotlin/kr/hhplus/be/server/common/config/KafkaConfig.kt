package kr.hhplus.be.server.common.config

import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_DATA_PLATFORM
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_RESERVATION
import kr.hhplus.be.server.common.kafka.KafkaGroupIdConst.Companion.GROUP_TEST
import kr.hhplus.be.server.common.kafka.KafkaTopicNameConst
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties.AckMode
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig(
	@Value("\${spring.kafka.listener.ack-mode}")
	private val ackMode: AckMode,

	@Value("\${spring.kafka.producer.bootstrap-servers}")
	private val producerHost: String,

	@Value("\${spring.kafka.consumer.bootstrap-servers}")
	private val consumerHost: String,
) {
	companion object {
		private const val BACKOFF_INTERVAL_SECOND = 100L
		private const val BACKOFF_MAX_RETRY = 3L
	}

	@Bean
	fun createTopics(): List<NewTopic> {
		return KafkaTopicNameConst.topics.map {
			TopicBuilder.name(it.key)
				.partitions(it.value)
				.replicas(1)
				.build()
		}
	}

	@Bean
	fun kafkaProducerFactory(): ProducerFactory<String, Any> =
		DefaultKafkaProducerFactory(
			mapOf(
				ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to producerHost,
				ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
				ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
			)
		)

	@Bean
	fun kafkaTemplate(): KafkaTemplate<String, Any> = KafkaTemplate(kafkaProducerFactory())

	private fun createConcurrentKafkaListenerFactory(consumerGroup: String): ConcurrentKafkaListenerContainerFactory<String, Any> {
		val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
		factory.consumerFactory = DefaultKafkaConsumerFactory(
			mapOf(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to consumerHost,
				ConsumerConfig.GROUP_ID_CONFIG to consumerGroup,
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
			)
		)
		factory.containerProperties.ackMode = this.ackMode

		val defaultErrorHandler = DefaultErrorHandler(
			DeadLetterPublishingRecoverer(kafkaTemplate()),
			FixedBackOff(BACKOFF_INTERVAL_SECOND, BACKOFF_MAX_RETRY) // 기본 backoff 정책
		)
		factory.setCommonErrorHandler(defaultErrorHandler)

		return factory
	}

	// 동작 테스트를 위한 컨슈머 그룹
	@Bean
	fun concurrentTestKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory(GROUP_TEST)

	/**
	 * MSA 환경을 가정하여, 각 도메인과 데이터플랫폼을 서로 다른 컨슈머 그룹으로 등록
	 */
	@Bean
	fun concurrentReservationKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory(GROUP_RESERVATION)

	@Bean
	fun concurrentPaymentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory("payment")

	@Bean
	fun concurrentConcertKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory("concert")

	@Bean
	fun concurrentUserKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory("user")

	@Bean
	fun concurrentDataPlatformKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> =
		createConcurrentKafkaListenerFactory(GROUP_DATA_PLATFORM)
}