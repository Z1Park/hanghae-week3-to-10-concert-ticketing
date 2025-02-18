package kr.hhplus.be.server.common.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties.AckMode

@Configuration
class KafkaConfig(
	@Value("\${spring.kafka.listener.ack-mode}")
	private val ackMode: AckMode,

	@Value("\${spring.kafka.producer.bootstrap-servers}")
	private val producerHost: String,

	@Value("\${spring.kafka.consumer.bootstrap-servers}")
	private val consumerHost: String,
) {

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

	@Bean
	fun kafkaConsumerFactory(): ConsumerFactory<String, Any> =
		DefaultKafkaConsumerFactory(
			mapOf(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to consumerHost,
				ConsumerConfig.GROUP_ID_CONFIG to "group1",
				ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
				ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
			)
		)

	@Bean
	fun concurrentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Any> {
		val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
		factory.consumerFactory = kafkaConsumerFactory()
		factory.containerProperties.ackMode = this.ackMode

		return factory
	}
}