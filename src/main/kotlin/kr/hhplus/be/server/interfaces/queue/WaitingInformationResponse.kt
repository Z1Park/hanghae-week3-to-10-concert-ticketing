package kr.hhplus.be.server.interfaces.queue

import kr.hhplus.be.server.domain.queue.QueueWaitingInfo

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
