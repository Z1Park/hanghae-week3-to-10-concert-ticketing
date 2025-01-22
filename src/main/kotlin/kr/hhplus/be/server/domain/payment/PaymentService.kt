package kr.hhplus.be.server.domain.payment

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class PaymentService(
	private val paymentRepository: PaymentRepository
) {

	@Transactional
	fun pay(command: PaymentCommand.Create): Payment {
		val existPayment = paymentRepository.findByUserIdAndReservationId(command.userId, command.reservationId)
		require(existPayment == null) { throw CustomException(ErrorCode.ALREADY_PAYED_RESERVATION, "reservationId=${command.reservationId}") }

		val payment = command.toPayment()
		return paymentRepository.save(payment)
	}

	@Transactional
	fun rollbackPayment(paymentId: Long) {
		val payment = paymentRepository.findById(paymentId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "paymentId=$paymentId")

		paymentRepository.delete(payment)
	}
}