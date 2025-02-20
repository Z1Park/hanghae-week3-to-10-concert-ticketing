package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.ReservationConfirmEvent
import kr.hhplus.be.server.application.event.ReservationFailEvent
import kr.hhplus.be.server.application.event.ReservationSuccessEvent
import kr.hhplus.be.server.domain.reservation.ReservationMessageProducer
import kr.hhplus.be.server.domain.reservation.ReservationOutboxService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ReservationEventListener(
	private val reservationOutboxService: ReservationOutboxService,
	private val reservationMessageProducer: ReservationMessageProducer,

	) {
	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun saveReservationInfoToOutbox(event: ReservationSuccessEvent) {
		log.debug("예약 정보 outbox 저장 : event=$event")

		reservationOutboxService.saveReservationDataPlatformMessage(event.toDataPlatformPayload())
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handleReservationSuccessEvent(event: ReservationSuccessEvent) {
		log.debug("예약 성공 - 데이터 플랫폼 전송 : event=$event")

		reservationMessageProducer.sendReservationDataPlatformMessage(event.toDataPlatformPayload())
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun handleReservationFailEvent(event: ReservationFailEvent) {
		log.warn("예약 실패 - 좌선 선점 내역 롤백 요청 메세지 발행 : event=$event")

		reservationMessageProducer.sendRollbackPreoccupyConcertSeatMessage(event.traceId)
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun saveReservationConfirmInfoToOutbox(event: ReservationConfirmEvent) {
		log.debug("예약 확정 정보 outbox 저장 : event=$event")

		reservationOutboxService.saveReservationConfirmInfo(event.toReservationConfirmPayload())
	}
}