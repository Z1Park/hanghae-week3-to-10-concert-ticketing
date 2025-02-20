package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ReservationOutboxRepository : JpaRepository<ReservationOutboxMessage, Long> {

	fun findByTraceId(traceId: String): ReservationOutboxMessage?

	fun findAllByEventStatusAndCreatedAtBefore(eventStatus: OutboxEventStatus, createdAt: LocalDateTime): List<ReservationOutboxMessage>
}