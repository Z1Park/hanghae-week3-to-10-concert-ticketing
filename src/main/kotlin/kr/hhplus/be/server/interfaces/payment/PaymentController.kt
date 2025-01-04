package kr.hhplus.be.server.interfaces.payment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.exception.ForbiddenException
import kr.hhplus.be.server.exception.UnauthorizedException
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.*

@Tag(name = "결제")
@RestController
@RequestMapping("/payments")
class PaymentController {

	@Operation(
		summary = "결제 요청 API",
		description = "예약 정보를 받아 결제를 진행",
	)
	@PostMapping("")
	fun pay(
		@CookieValue("user-access-token") userAccessToken: String?,
		@RequestBody payRequest: PayRequest
	) {
		require(payRequest.reservationId != 0) { throw BadRequestException() }
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
		require(payRequest.reservationId != -1) { throw ForbiddenException() }
	}
}