package kr.hhplus.be.server.interfaces.reservation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.reservation.ReservationFacadeService
import kr.hhplus.be.server.interfaces.resolver.QueueToken
import kr.hhplus.be.server.interfaces.resolver.UserToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}