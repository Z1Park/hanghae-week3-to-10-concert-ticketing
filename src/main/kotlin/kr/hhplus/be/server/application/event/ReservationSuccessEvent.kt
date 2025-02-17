package kr.hhplus.be.server.application.event

data class ReservationSuccessEvent(
	val concertSeatId: Long,
	val reservationId: Long
) {
}