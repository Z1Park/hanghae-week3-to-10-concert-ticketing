package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.user.PointUsePayload

data class PointUseSuccessEvent(
	val traceId: String,
	val userId: Long,
	val originBalance: Int,
	val pointHistoryId: Long
) {

	fun toPointUsePayload(): PointUsePayload =
		PointUsePayload(traceId, userId, originBalance, pointHistoryId)
}