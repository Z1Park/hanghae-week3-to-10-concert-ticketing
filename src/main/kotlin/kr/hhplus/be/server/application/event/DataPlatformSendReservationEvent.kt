package kr.hhplus.be.server.application.event

class DataPlatformSendReservationEvent(
	val reservationId: Long,
	val concertSeatId: Long
) {
}