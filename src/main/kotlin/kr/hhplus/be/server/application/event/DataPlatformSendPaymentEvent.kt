package kr.hhplus.be.server.application.event

data class DataPlatformSendPaymentEvent(
	val concertSeatId: Long,
	val reservationId: Long,
	val paymentId: Long
) {
}