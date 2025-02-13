package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.ConcertSeatPreoccupySuccessEvent
import kr.hhplus.be.server.application.event.ReservationConcertSeatFailEvent
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.reservation.ReservationCommand
import kr.hhplus.be.server.domain.reservation.ReservationService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ConcertSeatPreoccupySuccessEventListener(
	private val reservationService: ReservationService,
	private val clockHolder: ClockHolder,
	private val applicationEventPublisher: ApplicationEventPublisher
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handleConcertSeatPreoccupySuccessEvent(event: ConcertSeatPreoccupySuccessEvent) {
		val command = ReservationCommand.Create(
			event.price,
			event.userId,
			event.concertId,
			event.concertScheduleId,
			event.concertSeatId,
			event.expiredAt
		)

		try {
			reservationService.reserve(command, clockHolder)
		} catch (e: Exception) {
			log.error("예약 실패 및 롤백 시퀀스 실행 : ", e)

			applicationEventPublisher.publishEvent(ReservationConcertSeatFailEvent(event.concertSeatId, event.originExpiredAt))
			throw e
		}
	}
}