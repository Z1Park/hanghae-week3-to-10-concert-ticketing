package kr.hhplus.be.server.application.event

import kr.hhplus.be.server.domain.reservation.ReservationDataPlatformPayload
import java.time.LocalDateTime

data class ReservationSuccessEvent(
	val traceId: String,
	val concertSeatId: Long,
	val reservationId: Long,
	val userId: Long,
	val price: Int,
	val reservedAt: LocalDateTime
) {

	fun toDataPlatformPayload(): ReservationDataPlatformPayload {
		return ReservationDataPlatformPayload(
			traceId = traceId,
			reservationId = reservationId,
			userId = userId,
			concertSeatId = concertSeatId,
			price = price,
			reservedAt = reservedAt
		)
	}
}