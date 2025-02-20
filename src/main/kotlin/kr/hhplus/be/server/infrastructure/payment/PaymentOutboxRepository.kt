package kr.hhplus.be.server.infrastructure.payment

import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.infrastructure.payment.entity.PaymentOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface PaymentOutboxRepository : JpaRepository<PaymentOutboxMessage, Long> {

	fun findByTraceId(traceId: String): PaymentOutboxMessage?

	fun findAllByEventStatusAndCreatedAtBefore(eventStatus: OutboxEventStatus, createdAt: LocalDateTime): List<PaymentOutboxMessage>
}