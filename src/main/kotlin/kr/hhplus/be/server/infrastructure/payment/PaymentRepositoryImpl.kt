package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.domain.payment.PaymentRepository
import kr.hhplus.be.server.domain.payment.model.Payment
import kr.hhplus.be.server.infrastructure.payment.entity.PaymentEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
	private val paymentJpaRepository: PaymentJpaRepository
) : PaymentRepository {

	override fun findByUserIdAndReservationId(userId: Long, reservationId: Long): Payment? =
		paymentJpaRepository.findByUserIdAndReservationId(userId, reservationId)?.toDomain()

	override fun findById(paymentId: Long): Payment? =
		paymentJpaRepository.findByIdOrNull(paymentId)?.toDomain()

	override fun save(payment: Payment): Payment =
		paymentJpaRepository.save(PaymentEntity(payment)).toDomain()

	override fun delete(payment: Payment) =
		paymentJpaRepository.delete(PaymentEntity(payment))
}