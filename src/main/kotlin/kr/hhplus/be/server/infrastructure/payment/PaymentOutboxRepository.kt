package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.infrastructure.payment.entity.PaymentOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentOutboxRepository : JpaRepository<PaymentOutboxMessage, Long> {

	fun findByTraceId(traceId: String): PaymentOutboxMessage?
}