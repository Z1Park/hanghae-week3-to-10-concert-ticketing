package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.infrastructure.concert.entity.ConcertOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertOutboxRepository : JpaRepository<ConcertOutboxMessage, Long> {

	fun findByTraceId(traceId: String): ConcertOutboxMessage?
}