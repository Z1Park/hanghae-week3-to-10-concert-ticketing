package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.QueueRepository
import kr.hhplus.be.server.domain.queue.model.Queue
import kr.hhplus.be.server.domain.queue.model.QueueActiveStatus
import kr.hhplus.be.server.infrastructure.queue.entity.QueueJpaEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class QueueRepositoryImpl(
	private val queueJpaRepository: QueueJpaRepository
) : QueueRepository {
	override fun findById(tokenId: Long): Queue? = queueJpaRepository.findByIdOrNull(tokenId)?.toDomain()

	override fun findByUUID(tokenUUID: String): Queue? = queueJpaRepository.findByTokenUUID(tokenUUID)?.toDomain()

	override fun findAllByActivateStatusAndExpiredAtBefore(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): List<Queue> =
		queueJpaRepository.findAllByActivateStatusAndExpiredAtBefore(activateStatus, expiredAt).map { it.toDomain() }

	override fun countByActivateStatusAndExpiredAtAfter(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): Int =
		queueJpaRepository.countAllByActivateStatusAndExpiredAtAfter(activateStatus, expiredAt)

	override fun findAllOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> =
		queueJpaRepository.findAllByActivateStatusOrderByCreatedAt(activateStatus, pageable).map { it.toDomain() }

	override fun findAllOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> =
		queueJpaRepository.findAllByActivateStatusOrderByCreatedAtDesc(activateStatus, pageable).map { it.toDomain() }

	override fun save(queue: Queue): Queue = queueJpaRepository.save(QueueJpaEntity(queue)).toDomain()

	override fun saveAll(queues: List<Queue>) {
		queueJpaRepository.saveAll(queues.map { QueueJpaEntity(it) })
	}
}