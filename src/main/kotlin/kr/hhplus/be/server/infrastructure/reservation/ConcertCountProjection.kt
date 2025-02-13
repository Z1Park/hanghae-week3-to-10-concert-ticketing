package kr.hhplus.be.server.infrastructure.reservation

data class ConcertCountProjection(
	val concertId: Long,
	val concertCount: Long
) {
}