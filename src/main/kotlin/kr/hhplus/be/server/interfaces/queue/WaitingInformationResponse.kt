package kr.hhplus.be.server.interfaces.queue

data class WaitingInformationResponse(
    val myOrder: Long,
    val waitingMinutes: Int
)
