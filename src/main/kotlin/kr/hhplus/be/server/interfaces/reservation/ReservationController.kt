package kr.hhplus.be.server.interfaces.reservation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.interfaces.exception.ConflictException
import kr.hhplus.be.server.interfaces.exception.ForbiddenException
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

@Tag(name = "예약")
@RestController
@RequestMapping("/reservations")
class ReservationController {

	@Operation(
		summary = "예약 요청 API",
		description = "콘서트 좌석을 예약",
	)
	@PostMapping("")
	fun reserveConcertSeat(
		@CookieValue("user-access-token") userAccessToken: String?,
		@CookieValue("concert-access-token") concertAccessToken: String?,
		@RequestBody reserveConcertRequest: ReserveConcertRequest
	): ReserveConcertResponse {
		require(reserveConcertRequest.concertId != 0L) { throw BadRequestException() }
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
		require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }
		require(reserveConcertRequest.concertId != -1L) { throw ConflictException() }

		return ReserveConcertResponse(438L, ZonedDateTime.now().plusDays(3))
	}
}