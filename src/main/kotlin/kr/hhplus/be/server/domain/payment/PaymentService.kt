package kr.hhplus.be.server.domain.payment

import jakarta.transaction.Transactional
import kr.hhplus.be.server.application.event.PaymentCreateSuccessEvent
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.payment.model.Payment
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class PaymentService(
	private val paymentRepository: PaymentRepository,
	private val applicationEventPublisher: ApplicationEventPublisher
) {

	@Transactional
	fun pay(command: PaymentCommand.Create, traceId: String): Payment {
		val existPayment = paymentRepository.findByUserIdAndReservationId(command.userId, command.reservationId)
		require(existPayment == null) { throw CustomException(ErrorCode.ALREADY_PAYED_RESERVATION, "reservationId=${command.reservationId}") }

		val payment = paymentRepository.save(command.toPayment())
		applicationEventPublisher.publishEvent(
			PaymentCreateSuccessEvent(
				traceId,
				payment.id,
				payment.reservationId,
				payment.userId
			)
		)
		return payment
	}

	@Transactional
	fun rollbackPayment(paymentId: Long) {
		val payment = paymentRepository.findById(paymentId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "paymentId=$paymentId")

		paymentRepository.delete(payment)
	}
}