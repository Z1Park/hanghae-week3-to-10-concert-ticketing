package kr.hhplus.be.server.interfaces.token

import kr.hhplus.be.server.domain.token.QueueWaitingInfo

data class WaitingInformationResponse(
	val myWaitingOrder: Long,
	val expectedWaitingSeconds: Int
) {
	companion object {
		fun from(queueWaitingInfo: QueueWaitingInfo): WaitingInformationResponse {
			return WaitingInformationResponse(
				queueWaitingInfo.myWaitingOrder,
				queueWaitingInfo.expectedWaitingSeconds
			)
		}
	}
}
