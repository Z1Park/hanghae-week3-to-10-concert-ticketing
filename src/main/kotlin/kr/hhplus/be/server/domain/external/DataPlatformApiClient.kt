package kr.hhplus.be.server.domain.external

interface DataPlatformApiClient {

	fun send(concertSeatId: Long, reservationId: Long, paymentId: Long)
}