package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.domain.payment.Payment
import kr.hhplus.be.server.domain.payment.PaymentRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
	private val paymentJpaRepository: PaymentJpaRepository
) : PaymentRepository {

	override fun findByUserIdAndReservationId(userId: Long, reservationId: Long): Payment? =
		paymentJpaRepository.findByUserIdAndReservationId(userId, reservationId)

	override fun save(payment: Payment): Payment = paymentJpaRepository.save(payment)

	override fun delete(payment: Payment) = paymentJpaRepository.delete(payment)

	override fun findById(paymentId: Long): Payment? = paymentJpaRepository.findByIdOrNull(paymentId)
}