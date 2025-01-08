package kr.hhplus.be.server.domain.queue

import org.springframework.data.domain.Pageable

interface QueueRepository {

	fun findByUUID(tokenUUID: String): Queue?

	fun findAllFromLastActivatedQueue(pageable: Pageable): List<Queue>

	fun save(queue: Queue): Queue
}