package kr.hhplus.be.server.domain.concert

import java.time.LocalDateTime

class ConcertInfo {

	data class ConcertDto(
		val concertId: Long,
		val title: String,
		val provider: String
	) {
		companion object {
			fun from(concert: kr.hhplus.be.server.domain.concert.Concert): ConcertDto =
				ConcertDto(concert.id, concert.title, concert.provider)
		}
	}

	data class Schedule(
		val concertId: Long,
		val concertScheduleId: Long,
		val totalSeat: Int,
		val startAt: LocalDateTime,
		val endAt: LocalDateTime
	) {
		companion object {
			fun from(schedule: ConcertSchedule) =
				Schedule(schedule.concertId, schedule.id, schedule.totalSeat, schedule.startAt, schedule.endAt)
		}
	}

	data class Seat(
		val concertId: Long,
		val concertScheduleId: Long,
		val seatId: Long,
		val seatNumber: Int,
		val price: Int
	) {
		companion object {
			fun of(concertId: Long, seat: ConcertSeat) =
				Seat(concertId, seat.concertScheduleId, seat.id, seat.seatNumber, seat.price)
		}
	}

	data class ReservedSeat(
		val seatId: Long,
		val price: Int,
		val expiredAt: LocalDateTime
	) {
		companion object {
			fun of(concertSeat: ConcertSeat, expiredAt: LocalDateTime): ReservedSeat =
				ReservedSeat(
					concertSeat.id,
					concertSeat.price,
					expiredAt
				)
		}
	}
}
