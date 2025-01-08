package kr.hhplus.be.server.domain.queue

import org.springframework.data.domain.Pageable

interface QueueRepository {

	fun findByUUID(tokenUUID: String): Queue?

	fun findAllOrderByCreatedAt(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue>

	fun findAllOrderByCreatedAtDesc(activateStatus: QueueActiveStatus, pageable: Pageable): List<Queue>

	fun save(queue: Queue): Queue

	fun saveAll(queues: List<Queue>)
}