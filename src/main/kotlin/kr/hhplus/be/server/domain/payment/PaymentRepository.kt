package kr.hhplus.be.server.domain.payment

interface PaymentRepository {

	fun findByUserIdAndReservationId(userId: Long, reservationId: Long): Payment?

	fun save(payment: Payment): Payment

	fun delete(payment: Payment)
}