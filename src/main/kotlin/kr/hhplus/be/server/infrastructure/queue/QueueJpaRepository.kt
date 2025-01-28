package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.model.QueueActiveStatus
import kr.hhplus.be.server.infrastructure.queue.entity.QueueJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface QueueJpaRepository : JpaRepository<QueueJpaEntity, Long> {

	fun findByTokenUUID(tokenUUID: String): QueueJpaEntity?

	fun findAllByActivateStatusAndExpiredAtBefore(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): List<QueueJpaEntity>

	fun countAllByActivateStatusAndExpiredAtAfter(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): Int

	fun findAllByActivateStatusOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<QueueJpaEntity>

	fun findAllByActivateStatusOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<QueueJpaEntity>
}