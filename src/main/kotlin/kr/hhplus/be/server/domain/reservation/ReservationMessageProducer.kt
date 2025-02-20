package kr.hhplus.be.server.domain.reservation

interface ReservationMessageProducer {

	fun sendReservationDataPlatformMessage(payload: ReservationDataPlatformPayload)

	fun sendRollbackPreoccupyConcertSeatMessage(traceId: String)
}