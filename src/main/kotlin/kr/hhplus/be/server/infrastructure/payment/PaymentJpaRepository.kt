package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.infrastructure.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {

	fun findByUserIdAndReservationId(userId: Long, reservationId: Long): PaymentEntity?
}