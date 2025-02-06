package kr.hhplus.be.server.infrastructure.concert

import com.fasterxml.jackson.databind.ObjectMapper
import kr.hhplus.be.server.domain.concert.ConcertCacheRepository
import kr.hhplus.be.server.domain.concert.model.Concert
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class ConcertCacheRepositoryImpl(
	private val hashOperations: HashOperations<String, String, String>,
	private val redisTemplate: RedisTemplate<String, String>,
	objectMapper: ObjectMapper
) : ConcertCacheRepository {

	companion object {
		private const val CONCERT_CACHE_KEY = "concertCache"
		private const val CONCERT_CACHE_HASH_PREFIX = "concertKey:"
		private lateinit var objectMapper: ObjectMapper

		fun init(objectMapper: ObjectMapper) {
			this.objectMapper = objectMapper
		}

		fun toConcertEntity(concertString: String): ConcertEntity =
			objectMapper.readValue(concertString, ConcertEntity::class.java)

		fun toStringValue(concertEntity: ConcertEntity): String =
			objectMapper.writeValueAsString(concertEntity)
	}

	init {
		init(objectMapper)
	}

	override fun findAllCacheConcerts(): List<Concert> =
		hashOperations.entries(CONCERT_CACHE_KEY).map { toConcertEntity(it.value).toDomain() }

	override fun saveCacheConcert(concert: Concert, key: String, timeToLiveSeconds: Long) {
		val concertEntity = ConcertEntity(concert)
		val value = toStringValue(concertEntity)

		redisTemplate.delete(CONCERT_CACHE_KEY)
		hashOperations.put(CONCERT_CACHE_KEY, CONCERT_CACHE_HASH_PREFIX + key, value)
		redisTemplate.expire(CONCERT_CACHE_KEY, timeToLiveSeconds, TimeUnit.SECONDS)
	}
}