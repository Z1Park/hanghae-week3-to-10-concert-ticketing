package kr.hhplus.be.server.infrastructure.reservation

import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationOutboxRepository : JpaRepository<ReservationOutboxMessage, Long> {

	fun findByTraceId(traceId: String): ReservationOutboxMessage?
}