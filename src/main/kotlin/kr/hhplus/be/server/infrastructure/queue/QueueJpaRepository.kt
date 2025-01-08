package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueActiveStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface QueueJpaRepository : JpaRepository<Queue, Long> {

	fun findByTokenUUID(tokenUUID: String): Queue?

	fun findAllByActivateStatusAndExpiredAtBefore(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): List<Queue>

	fun countAllByActivateStatusAndExpiredAtAfter(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): Int

	fun findAllByActivateStatusOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue>

	fun findAllByActivateStatusOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue>
}