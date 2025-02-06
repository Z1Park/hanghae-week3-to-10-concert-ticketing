package kr.hhplus.be.server.domain.concert

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Duration
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ConcertCacheServiceUnitTest {

	@InjectMocks
	private lateinit var sut: ConcertCacheService

	@Mock
	private lateinit var concertCacheRepository: ConcertCacheRepository

	@Test
	fun `인기 콘서트 캐시 업데이트 시, ConcertInfo의 hashCode를 key로 하면서 다음날 00시 10분 00초에 만료하도록 캐시를 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 7, 2, 48, 39)
		val concertDto = ConcertInfo.ConcertDto(1L, "콘서트", "가수", false)
		val concertDtos = listOf(concertDto)

		// when
		sut.saveTopConcertInfo(concertDtos) { testTime }

		//then
		val expiredAt = testTime.plusDays(1).withHour(0).withMinute(10).withSecond(0)
		val expectedTTL = Duration.between(testTime, expiredAt).toSeconds()

		verify(concertCacheRepository).saveCacheConcert(concertDto.toConcert(), concertDto.hashCode().toString(), expectedTTL)
	}
}