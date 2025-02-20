package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.application.event.DataPlatformSendPaymentEvent
import kr.hhplus.be.server.application.event.PaymentFailEvent
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentCommand
import kr.hhplus.be.server.domain.payment.PaymentService
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
	private val uuidGenerator: UuidGenerator,
	private val clockHolder: ClockHolder,
	private val applicationEventPublisher: ApplicationEventPublisher
) {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 코레오그래피 패턴을 통해 예약 로직 수행
	 */
	fun reserveConcertSeat(requestCri: ReservationCri.Create) {
		val traceId = uuidGenerator.generateUuid()
		reservationService.reserve(requestCri.toReservationCommandCreate(), traceId, clockHolder)
	}

	/**
	 * 오케스트레이터 패턴을 통해 결제 흐름 제어
	 */
	fun payReservation(paymentCri: PaymentCri.Create) {
		val traceId = uuidGenerator.generateUuid()

		val user = userService.getByUuid(paymentCri.userUUID)
		val reservation = reservationService.getReservationForPay(paymentCri.reservationId, clockHolder)

		try {
			userService.use(traceId, paymentCri.userUUID, reservation.price)

			val command = PaymentCommand.Create(reservation.price, user.id, reservation.id)
			val pay = paymentService.pay(command, traceId)

			reservationService.confirmReservation(reservation.id, traceId)

			concertService.makeSoldOutConcertSeat(reservation.concertSeatId, traceId)

			applicationEventPublisher.publishEvent(
				DataPlatformSendPaymentEvent(traceId, pay.id, reservation.id, user.id, reservation.price)
			)
		} catch (e: Exception) {
			log.error("결제 실패 및 롤백 시퀀스 실행 : ", e)

			applicationEventPublisher.publishEvent(PaymentFailEvent(traceId))
			throw e
		}
	}
}