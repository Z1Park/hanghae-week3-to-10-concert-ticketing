package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentCommand
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.token.TokenService
import kr.hhplus.be.server.domain.reservation.ReservationCommand
import kr.hhplus.be.server.domain.reservation.ReservationPaymentFlow
import kr.hhplus.be.server.domain.reservation.ReservationPaymentOrchestrator
import kr.hhplus.be.server.domain.reservation.ReservationService
import kr.hhplus.be.server.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ReservationFacadeService(
	private val reservationService: ReservationService,
	private val userService: UserService,
	private val concertService: ConcertService,
	private val paymentService: PaymentService,
	private val tokenService: TokenService,
	private val orchestrator: ReservationPaymentOrchestrator,
	private val clockHolder: ClockHolder
) {
	private val log = LoggerFactory.getLogger(javaClass)

	fun reserveConcertSeat(requestCri: ReservationCri.Create): ReservationResult {
		val user = userService.getByUuid(requestCri.userUUID)

		val reservedSeatInfo = concertService.preoccupyConcertSeat(requestCri.toConcertCommandTotal(), clockHolder)

		val command = ReservationCommand.Create(
			reservedSeatInfo.price,
			user.id,
			requestCri.concertId,
			requestCri.concertScheduleId,
			reservedSeatInfo.seatId,
			reservedSeatInfo.expiredAt
		)
		try {
			val reservation = reservationService.reserve(command, clockHolder)

			return ReservationResult.from(reservation)
		} catch (e: Exception) {
			log.error("예약 실패 및 롤백 시퀀스 실행 : ", e)

			concertService.rollbackPreoccupyConcertSeat(reservedSeatInfo.seatId, reservedSeatInfo.originExpiredAt)
			throw e
		}
	}

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

		} catch (e: Exception) {
			log.error("결제 실패 및 롤백 시퀀스 실행 : ", e)

			orchestrator.rollbackAll()
			throw e

		} finally {
			orchestrator.clear()
		}
	}
}