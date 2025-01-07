package kr.hhplus.be.server.infrastructure.queue

import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueRepository
import org.springframework.stereotype.Repository

@Repository
class QueueRepositoryImpl(
	private val queueJpaRepository: QueueJpaRepository
) : QueueRepository {
	override fun save(queue: Queue): Queue = queueJpaRepository.save(queue)
}