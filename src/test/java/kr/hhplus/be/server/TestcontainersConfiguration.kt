package kr.hhplus.be.server

import jakarta.annotation.PreDestroy
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

@Configuration
@Testcontainers
class TestcontainersConfiguration {
	@PreDestroy
	fun preDestroy() {
		if (mySqlContainer.isRunning) mySqlContainer.stop()
		if (kafkaContainer.isRunning) kafkaContainer.stop()
	}

	companion object {
		val mySqlContainer: MySQLContainer<*> = MySQLContainer(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withInitScript("db/init.sql")
			.withUsername("test")
			.withPassword("test")
			.apply {
				start()
			}

		val kafkaImage: DockerImageName = DockerImageName.parse("confluentinc/cp-kafka:6.2.1")
		val kafkaContainer: KafkaContainer = KafkaContainer(kafkaImage.asCompatibleSubstituteFor("apache/kafka"))

		@JvmStatic
		@DynamicPropertySource
		fun kafkaProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
		}

		init {
			System.setProperty("spring.datasource.url", mySqlContainer.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC")
			System.setProperty("spring.datasource.username", mySqlContainer.username)
			System.setProperty("spring.datasource.password", mySqlContainer.password)
		}
	}
}
