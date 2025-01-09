package kr.hhplus.be.server.application.payment

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.payment.PaymentCommand
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.domain.reservation.ReservationService
import kr.hhplus.be.server.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PaymentFacadeService(
	private val paymentService: PaymentService,
	private val userService: UserService,
	private val reservationService: ReservationService,
	private val queueService: QueueService,
	private val clockHolder: ClockHolder
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	fun payReservation(paymentCri: PaymentCri.Create) {
		val user = userService.getByUuid(paymentCri.userUUID)

		val reservation = reservationService.getReservationForPay(user.id, paymentCri.reservationId, clockHolder)

		val command = PaymentCommand.Create(reservation.price, user.id, reservation.id)
		val payment = paymentService.pay(command)

		try {
			userService.use(paymentCri.userUUID, payment.price)
		} catch (e: Exception) {
			paymentService.rollbackPayment(payment)

			logger.error("rollback payment by error caused : ", e)
			throw e
		}

		reservationService.makeSoldOut(reservation)

		queueService.deactivateToken(paymentCri.tokenUUID)
	}
}