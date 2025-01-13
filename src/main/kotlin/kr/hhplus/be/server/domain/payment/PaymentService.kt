package kr.hhplus.be.server.domain.payment

import jakarta.transaction.Transactional
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class PaymentService(
	private val paymentRepository: PaymentRepository
) {

	@Transactional
	fun pay(command: PaymentCommand.Create): Payment {
		val existPayment = paymentRepository.findByUserIdAndReservationId(command.userId, command.reservationId)
		require(existPayment == null) { throw BadRequestException() }

		val payment = command.toPayment()
		return paymentRepository.save(payment)
	}

	@Transactional
	fun rollbackPayment(payment: Payment) = paymentRepository.delete(payment)
}