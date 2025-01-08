package kr.hhplus.be.server.interfaces.concert

data class ConcertSeatResponse(
	val concertSchedules: MutableList<ConcertSeatDto>
)

data class ConcertSeatDto(
	val seatId: Long,
	val seatNumber: Int,
	val price: Int
)
