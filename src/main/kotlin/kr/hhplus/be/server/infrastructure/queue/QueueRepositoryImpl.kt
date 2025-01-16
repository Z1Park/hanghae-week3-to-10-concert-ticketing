package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueActiveStatus
import kr.hhplus.be.server.domain.queue.QueueRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class QueueRepositoryImpl(
	private val queueJpaRepository: QueueJpaRepository
) : QueueRepository {
	override fun findById(tokenId: Long): Queue? = queueJpaRepository.findByIdOrNull(tokenId)

	override fun findByUUID(tokenUUID: String): Queue? = queueJpaRepository.findByTokenUUID(tokenUUID)

	override fun findAllByActivateStatusAndExpiredAtBefore(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): List<Queue> =
		queueJpaRepository.findAllByActivateStatusAndExpiredAtBefore(activateStatus, expiredAt)

	override fun countByActivateStatusAndExpiredAtAfter(activateStatus: QueueActiveStatus, expiredAt: LocalDateTime): Int =
		queueJpaRepository.countAllByActivateStatusAndExpiredAtAfter(activateStatus, expiredAt)

	override fun findAllOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> =
		queueJpaRepository.findAllByActivateStatusOrderByCreatedAt(activateStatus, pageable)

	override fun findAllOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> =
		queueJpaRepository.findAllByActivateStatusOrderByCreatedAtDesc(activateStatus, pageable)

	override fun save(queue: Queue): Queue = queueJpaRepository.save(queue)

	override fun saveAll(queues: List<Queue>) {
		queueJpaRepository.saveAll(queues)
	}
}