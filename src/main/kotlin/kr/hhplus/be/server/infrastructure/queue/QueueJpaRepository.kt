package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.Queue
import org.springframework.data.jpa.repository.JpaRepository

interface QueueJpaRepository : JpaRepository<Queue, Long> {
}