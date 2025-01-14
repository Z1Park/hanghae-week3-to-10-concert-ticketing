package kr.hhplus.be.server.interfaces.reservation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.reservation.PaymentCri
import kr.hhplus.be.server.application.reservation.ReservationFacadeService
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.common.resolver.UserToken
import org.springframework.web.bind.annotation.*

@Tag(name = "예약")
@RestController
@RequestMapping("/reservations")
class ReservationController(
	private val reservationFacadeService: ReservationFacadeService
) {

	@Operation(
		summary = "예약 요청 API",
		description = "콘서트 좌석을 예약",
	)
	@PostMapping("")
	fun reserveConcertSeat(
		@UserToken userUUID: String,
		@QueueToken tokenUUID: String,
		@RequestBody reservationRequest: ConcertReservationRequest
	): ConcertReservationResponse {
		val reservationResult = reservationFacadeService.reserveConcertSeat(reservationRequest.toReservationCriCreate(userUUID))

		return ConcertReservationResponse.from(reservationResult)
	}

	@Operation(
		summary = "결제 요청 API",
		description = "좌석 예약을 받아 결제를 진행",
	)
	@PostMapping("{reservationId}/pay")
	fun payReservation(
		@UserToken userUUID: String,
		@QueueToken tokenUUID: String,
		@PathVariable reservationId: Long
	) {
		val paymentCri = PaymentCri.Create(userUUID, tokenUUID, reservationId)
		reservationFacadeService.payReservation(paymentCri)
	}
}