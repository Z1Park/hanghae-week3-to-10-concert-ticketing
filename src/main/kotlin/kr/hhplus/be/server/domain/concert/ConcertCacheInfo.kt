package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.domain.concert.model.Concert

class ConcertCacheInfo {

	data class TopConcert(
		val id: Long,
		val title: String,
		val provider: String,
		val finished: Boolean
	) {
		companion object {
			fun from(concert: Concert): TopConcert = TopConcert(
				concert.id,
				concert.title,
				concert.provider,
				concert.finished
			)
		}
	}
}