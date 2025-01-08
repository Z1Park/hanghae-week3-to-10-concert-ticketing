package kr.hhplus.be.server.domain.concert

import java.time.LocalDateTime

class ConcertInfo {

	data class Concert(
		val concertId: Long,
		val title: String,
		val provider: String
	) {
		companion object {
			fun from(concert: kr.hhplus.be.server.domain.concert.Concert): Concert =
				Concert(concert.id, concert.title, concert.provider)
		}
	}

	data class Schedule(
		val concertScheduleId: Long,
		val concertPlace: String,
		val concertLocation: String,
		val totalSeat: Int,
		val startAt: LocalDateTime,
		val endAt: LocalDateTime
	)

	data class Seat(
		val seatId: Long,
		val seatNumber: Int,
		val price: Int
	)
}
