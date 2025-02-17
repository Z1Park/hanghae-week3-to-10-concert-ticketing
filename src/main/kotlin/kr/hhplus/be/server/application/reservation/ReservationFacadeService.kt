package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.application.event.DataPlatformSendPaymentEvent
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentCommand
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.reservation.ReservationPaymentFlow
import kr.hhplus.be.server.domain.reservation.ReservationPaymentOrchestrator
import kr.hhplus.be.server.domain.reservation.ReservationService
import kr.hhplus.be.server.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class ReservationFacadeService(
	private val reservationService: ReservationService,
	private val userService: UserService,
	private val concertService: ConcertService,
	private val paymentService: PaymentService,
	private val orchestrator: ReservationPaymentOrchestrator,
	private val clockHolder: ClockHolder,
	private val applicationEventPublisher: ApplicationEventPublisher
) {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 코레오그래피 패턴을 통해 예약 로직 수행
	 * preoccupyConcertSeat 호출 -> ConcertSeatPreoccupySuccessEvent 수행
	 * ConcertSeatPreoccupySuccessEvent 실패 시 보상 트랜잭션 ->  ReservationConcertSeatFailEvent 수행
	 */
	fun reserveConcertSeat(requestCri: ReservationCri.Create) {
		reservationService.reserve(requestCri.toReservationCommandCreate(), clockHolder)
	}

	/**
	 * 오케스트레이터 패턴을 통해 결제 흐름 제어
	 */
	fun payReservation(paymentCri: PaymentCri.Create) {
		val user = userService.getByUuid(paymentCri.userUUID)
		val reservation = reservationService.getReservationForPay(paymentCri.reservationId, clockHolder)

		val reservationExpiredAt = reservation.expiredAt
		orchestrator.setupInitialRollbackInfo(user.id, reservationExpiredAt)

		try {
			val pointHistory = userService.use(paymentCri.userUUID, reservation.price)
			orchestrator.successFlow(ReservationPaymentFlow.USE_POINT, pointHistory.id)

			val command = PaymentCommand.Create(reservation.price, user.id, reservation.id)
			val pay = paymentService.pay(command)
			orchestrator.successFlow(ReservationPaymentFlow.CREATE_PAYMENT, pay.id)

			reservationService.makeSoldOut(reservation.id)
			orchestrator.successFlow(ReservationPaymentFlow.SOLD_OUT_RESERVATION, reservation.id)

			val concertSeat = concertService.makeSoldOutConcertSeat(reservation.concertSeatId)
			orchestrator.successFlow(ReservationPaymentFlow.SOLD_OUT_SEAT, concertSeat.id)

			applicationEventPublisher.publishEvent(
				DataPlatformSendPaymentEvent(concertSeat.id, reservation.id, pay.id)
			)
		} catch (e: Exception) {
			log.error("결제 실패 및 롤백 시퀀스 실행 : ", e)

			orchestrator.rollbackAll()
			throw e

		} finally {
			orchestrator.clear()
		}
	}
}