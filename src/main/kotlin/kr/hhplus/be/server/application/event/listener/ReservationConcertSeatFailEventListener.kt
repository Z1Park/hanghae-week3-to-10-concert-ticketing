package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.ReservationConcertSeatFailEvent
import kr.hhplus.be.server.domain.concert.ConcertService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ReservationConcertSeatFailEventListener(
	private val concertService: ConcertService
) {

	@EventListener
	fun handleReservationConcertSeatFailEvent(event: ReservationConcertSeatFailEvent) {
		concertService.rollbackPreoccupyConcertSeat(event.concertSeatId, event.originExpiredAt)
	}
}