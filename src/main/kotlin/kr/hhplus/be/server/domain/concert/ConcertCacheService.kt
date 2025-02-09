package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.common.component.ClockHolder
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class ConcertCacheService(
	private val concertCacheRepository: ConcertCacheRepository
) {

	fun getTopConcertInfo(): List<ConcertInfo.ConcertDto>? {
		if (!concertCacheRepository.isExistCacheConcerts()) {
			return null
		}

		val topConcerts = concertCacheRepository.findAllCacheConcerts()
		return topConcerts.map { ConcertInfo.ConcertDto.from(it) }
	}

	/**
	 * 만료는 현재 시간과 관계 없이 항상 다음날 00:10:00
	 */
	fun saveTopConcertInfo(topConcertInfos: List<ConcertInfo.ConcertDto>, clockHolder: ClockHolder) {
		val currentTime = clockHolder.getCurrentTime()
		val expiredAt = currentTime.plusDays(1).withHour(0).withMinute(10).withSecond(0)
		val timeToLiveSeconds = Duration.between(currentTime, expiredAt).toSeconds()

		concertCacheRepository.deleteCacheConcert()
		topConcertInfos.forEach {
			concertCacheRepository.saveCacheConcert(it.toConcert(), it.hashCode().toString(), timeToLiveSeconds)
		}
	}
}