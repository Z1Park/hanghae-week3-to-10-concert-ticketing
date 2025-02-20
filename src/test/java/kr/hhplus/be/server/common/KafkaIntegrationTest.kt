package kr.hhplus.be.server.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest
class KafkaIntegrationTest(
	@Autowired private val kafkaTemplate: KafkaTemplate<String, Any>,
) {
	private val countDownLatch = CountDownLatch(1)
	private var receivedMessage: String = ""

	@KafkaListener(groupId = "test", topics = ["test-topic"])
	fun receive(message: String) {
		receivedMessage = message
		println("Kafka 테스트 메세지 수신 : message = ${message}")
		countDownLatch.countDown()
	}

	@Test
	fun `카프카에 메세지를 보내고 Listener를 통해 메세지를 수신할 수 있는지 테스트한다`() {
		// given
		val message = "Seokbum is GOD"

		// when
		kafkaTemplate.send("test-topic", message)

		//then
		countDownLatch.await(10, TimeUnit.SECONDS) // 10초간 메세지 수신 대기
		assertThat(message).isEqualTo("Seokbum is GOD")
	}
}