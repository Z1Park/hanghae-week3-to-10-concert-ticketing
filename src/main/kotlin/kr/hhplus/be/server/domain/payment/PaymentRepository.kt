package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.domain.payment.model.Payment

interface PaymentRepository {

	fun findByUserIdAndReservationId(userId: Long, reservationId: Long): Payment?

	fun save(payment: Payment): Payment

	fun delete(payment: Payment)

	fun findById(paymentId: Long): Payment?
}