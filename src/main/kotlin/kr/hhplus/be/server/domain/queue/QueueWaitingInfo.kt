package kr.hhplus.be.server.domain.queue

data class QueueWaitingInfo(
	val myWaitingOrder: Long,
	val expectedWaitingSeconds: Int
)
