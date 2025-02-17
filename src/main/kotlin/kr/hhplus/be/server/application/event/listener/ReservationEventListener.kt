package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.ReservationFailEvent
import kr.hhplus.be.server.application.event.ReservationSuccessEvent
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.external.DataPlatformApiClient
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
	private val concertService: ConcertService,
	private val dataPlatformApiClient: DataPlatformApiClient

) {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 예약 완료 이후 데이터 플랫폼으로 예약 내역 전송
	 */
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handleReservationSuccessEvent(event: ReservationSuccessEvent) {
		try {
			dataPlatformApiClient.send(event.concertSeatId, event.reservationId)
		} catch (e: Exception) {
			log.error("예약 정보 데이터플랫폼 전달 실패 : ", e)
		}
	}

	@EventListener
	fun handleReservationFailEvent(event: ReservationFailEvent) {
		concertService.rollbackPreoccupyConcertSeat(event.concertSeatId, event.originExpiredAt)
	}
}