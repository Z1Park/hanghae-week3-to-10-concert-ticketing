package kr.hhplus.be.server.interfaces.event

import kr.hhplus.be.server.application.concert.ConcertFacadeService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TopConcertInfoUpdateEventListener(
	private val concertFacadeService: ConcertFacadeService
) {

	@EventListener
	fun updateTopConcertInfo(topConcertInfoUpdateEvent: TopConcertInfoUpdateEvent) {
		concertFacadeService.updateYesterdayTopConcertInfo()
	}
}