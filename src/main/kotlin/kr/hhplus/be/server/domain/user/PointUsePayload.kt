package kr.hhplus.be.server.domain.user

data class PointUsePayload(
	val traceId: String,
	val userId: Long,
	val originBalance: Int?,
	val pointHistoryId: Long?
)
