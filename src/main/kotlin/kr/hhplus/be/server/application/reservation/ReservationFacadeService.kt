package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentCommand
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.domain.reservation.ReservationCommand
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
	private val queueService: QueueService,
	private val clockHolder: ClockHolder
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun reserveConcertSeat(requestCri: ReservationCri.Create): ReservationResult {
		val user = userService.getByUuid(requestCri.userUUID)

		val seatTotalInfo = concertService.getConcertSeatTotalInformation(requestCri.toConcertCommandTotal())

		val createRequest = ReservationCommand.Create(
			seatTotalInfo.price,
			user.id,
			seatTotalInfo.concertId,
			seatTotalInfo.concertScheduleId,
			seatTotalInfo.seatId
		)
		val reservation = reservationService.reserve(createRequest, clockHolder)
		return ReservationResult.from(reservation)
	}

	fun payReservation(paymentCri: PaymentCri.Create) {
		val user = userService.getByUuid(paymentCri.userUUID)

		val reservation = reservationService.getReservationForPay(user.id, paymentCri.reservationId, clockHolder)

		val command = PaymentCommand.Create(reservation.price, user.id, reservation.id)
		val payment = paymentService.pay(command)

		runCatching {
			userService.use(paymentCri.userUUID, payment.price)
		}.onSuccess {
			reservationService.makeSoldOut(reservation)

			queueService.deactivateToken(paymentCri.tokenUUID)
		}.onFailure { e ->
			paymentService.rollbackPayment(payment)

			logger.error("rollback payment by error caused : ", e)
			throw e
		}.getOrThrow()
	}
}