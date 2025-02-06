package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.domain.concert.model.Concert

interface ConcertCacheRepository {

	fun findAllCacheConcerts(): List<Concert>

	fun saveCacheConcert(concert: Concert, key: String, timeToLiveSeconds: Long)
}