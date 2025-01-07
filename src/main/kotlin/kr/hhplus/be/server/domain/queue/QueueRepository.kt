package kr.hhplus.be.server.domain.queue

interface QueueRepository {
	fun save(queue: Queue): Queue
}