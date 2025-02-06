package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.concert.ConcertCacheRepositoryImpl
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@SpringBootTest
class ConcertCacheServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ConcertCacheService,
	@Autowired private val hashOperations: HashOperations<String, String, String>,
	@Autowired private val redisTemplate: RedisTemplate<String, String>
) {

	private fun toConcertEntity(concertString: String): ConcertEntity =
		ConcertCacheRepositoryImpl.toConcertEntity(concertString)

	private fun toStringValue(concertEntity: ConcertEntity): String =
		ConcertCacheRepositoryImpl.toStringValue(concertEntity)

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `인기 콘서트 캐시 조회 시, 캐시에 저장되어 있는 콘서트 정보를 불러와 반환한다`() {
		// given
		val concertDto1 = ConcertInfo.ConcertDto(1L, "title1", "provider1", false)
		hashOperations.put(
			"concertCache",
			"concertId:${concertDto1.hashCode()}",
			"{\"id\":1,\"createdAt\":[2025,2,6,18,23,46,16048000],\"updatedAt\":[2025,2,6,18,23,46,16060000],\"title\":\"title1\",\"provider\":\"provider1\",\"finished\":false}"
		)
		val concertDto2 = ConcertInfo.ConcertDto(2L, "title2", "provider2", false)
		hashOperations.put(
			"concertCache",
			"concertId:${concertDto2.hashCode()}",
			"{\"id\":2,\"createdAt\":[2025,2,6,18,23,46,16048000],\"updatedAt\":[2025,2,6,18,23,46,16060000],\"title\":\"title2\",\"provider\":\"provider2\",\"finished\":false}"
		)
		val concertDto3 = ConcertInfo.ConcertDto(3L, "title3", "provider3", false)
		hashOperations.put(
			"concertCache",
			"concertId:${concertDto3.hashCode()}",
			"{\"id\":3,\"createdAt\":[2025,2,6,18,23,46,16048000],\"updatedAt\":[2025,2,6,18,23,46,16060000],\"title\":\"title3\",\"provider\":\"provider3\",\"finished\":false}"
		)

		// when
		val actual = sut.getTopConcertInfo()

		//then
		assertThat(actual).hasSize(3)
			.anyMatch { it.id == 1L && it.title == "title1" }
			.anyMatch { it.id == 2L && it.title == "title2" }
			.anyMatch { it.id == 3L && it.title == "title3" }
	}

	@Test
	fun `인기 콘서트 캐시 저장 시, 콘서트를 캐시에 저장하고 만료시간이 다음날 00시 10분 00초로 되어있다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 7, 23, 50, 0)
		val concertDto1 = ConcertInfo.ConcertDto(1L, "title1", "provider1", false)
		val concertDto2 = ConcertInfo.ConcertDto(2L, "title2", "provider2", false)
		val concertDto3 = ConcertInfo.ConcertDto(3L, "title3", "provider3", false)

		// when
		sut.saveTopConcertInfo(listOf(concertDto1, concertDto2, concertDto3)) { testTime }

		//then
		val actual = hashOperations.size("concertCache")
		assertThat(actual).isEqualTo(3)

		val expected = testTime.plusDays(1).withHour(0).withMinute(10).withSecond(0)
		redisTemplate.expireAt("concertCache", Date.from(expected.toInstant(ZoneOffset.UTC)))
	}
}