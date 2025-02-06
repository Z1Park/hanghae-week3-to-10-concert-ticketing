package kr.hhplus.be.server.domain.token

data class QueueWaitingInfo(
	val myWaitingOrder: Long,
	val expectedWaitingSeconds: Int
)
