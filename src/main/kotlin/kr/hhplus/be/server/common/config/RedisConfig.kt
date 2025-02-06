package kr.hhplus.be.server.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig(
	@Value("\${spring.data.redis.port}")
	private val port: Int,

	@Value("\${spring.data.redis.host}")
	private val host: String
) {

	@Bean
	fun redisConnectionFactory(): RedisConnectionFactory = LettuceConnectionFactory(host, port)

	@Bean
	fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
		RedisTemplate<String, String>().apply {
			this.connectionFactory = connectionFactory

			keySerializer = StringRedisSerializer()
			valueSerializer = StringRedisSerializer()
			hashKeySerializer = StringRedisSerializer()

			afterPropertiesSet()
		}

	@Bean
	fun redisZSetOperations(redisTemplate: RedisTemplate<String, String>): ZSetOperations<String, String> =
		redisTemplate.opsForZSet()

	@Bean
	fun redisValueOperations(redisTemplate: RedisTemplate<String, String>): ValueOperations<String, String> =
		redisTemplate.opsForValue()

	@Bean
	fun redisHashOperations(redisTemplate: RedisTemplate<String, String>): HashOperations<String, String, String> =
		redisTemplate.opsForHash()
}