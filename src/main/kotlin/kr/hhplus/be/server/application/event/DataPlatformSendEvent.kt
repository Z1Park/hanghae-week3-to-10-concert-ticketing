package kr.hhplus.be.server.application.event

data class DataPlatformSendEvent(
	val concertSeatId: Long,
	val reservationId: Long,
	val paymentId: Long
) {
}