package kr.hhplus.be.server.interfaces.concert.dto

import kr.hhplus.be.server.domain.concert.ConcertInfo

data class ConcertSeatResponse(
	val concertSchedules: List<ConcertSeatDto>
) {
	companion object {
		fun from(concertSeatInformation: List<ConcertInfo.Seat>): ConcertSeatResponse =
			ConcertSeatResponse(concertSeatInformation.map { ConcertSeatDto.from(it) })
	}
}

data class ConcertSeatDto(
	val concertId: Long,
	val concertScheduleId: Long,
	val seatId: Long,
	val seatNumber: Int,
	val price: Int
) {
	companion object {
		fun from(seat: ConcertInfo.Seat): ConcertSeatDto =
			ConcertSeatDto(seat.concertId, seat.concertScheduleId, seat.seatId, seat.seatNumber, seat.price)
	}
}
