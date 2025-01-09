package kr.hhplus.be.server.interfaces.reservation

import kr.hhplus.be.server.application.reservation.ReservationCri

data class ConcertReservationRequest(
	val concertId: Long,
	val concertScheduleId: Long,
	val concertSeatId: Long
) {
	fun toReservationCriCreate(userUUID: String): ReservationCri.Create =
		ReservationCri.Create(userUUID, concertId, concertScheduleId, concertSeatId)
}
