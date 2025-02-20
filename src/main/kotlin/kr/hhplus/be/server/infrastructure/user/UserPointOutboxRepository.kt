package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.infrastructure.user.entity.UserPointOutboxMessage
import org.springframework.data.jpa.repository.JpaRepository

interface UserPointOutboxRepository : JpaRepository<UserPointOutboxMessage, Long> {
	fun findByTraceId(traceId: String): UserPointOutboxMessage?
}