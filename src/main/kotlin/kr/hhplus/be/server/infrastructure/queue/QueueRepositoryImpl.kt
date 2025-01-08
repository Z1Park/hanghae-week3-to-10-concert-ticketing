package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueActiveStatus
import kr.hhplus.be.server.domain.queue.QueueRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class QueueRepositoryImpl(
	private val queueJpaRepository: QueueJpaRepository
) : QueueRepository {

	override fun findByUUID(tokenUUID: String): Queue? {
		return queueJpaRepository.findByTokenUUID(tokenUUID)
	}

	override fun findAllOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> {
		return queueJpaRepository.findAllByActivateStatusOrderByCreatedAt(activateStatus, pageable)
	}

	override fun findAllOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue> {
		return queueJpaRepository.findAllByActivateStatusOrderByCreatedAtDesc(activateStatus, pageable)
	}

	override fun save(queue: Queue): Queue = queueJpaRepository.save(queue)

	override fun saveAll(queues: List<Queue>) {
		queueJpaRepository.saveAll(queues)
	}
}