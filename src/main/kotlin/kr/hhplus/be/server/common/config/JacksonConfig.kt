package kr.hhplus.be.server.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {

	@Bean
	fun objectMapper(): ObjectMapper {
		val objectMapper = ObjectMapper()
		objectMapper.registerModule(JavaTimeModule())
		return objectMapper
	}
}