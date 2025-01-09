package kr.hhplus.be.server.interfaces.payment

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.payment.PaymentFacadeService
import kr.hhplus.be.server.interfaces.resolver.QueueToken
import kr.hhplus.be.server.interfaces.resolver.UserToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "결제")
@RestController
@RequestMapping("/payments")
class PaymentController(
	private val paymentFacadeService: PaymentFacadeService
) {

	@Operation(
		summary = "결제 요청 API",
		description = "예약 정보를 받아 결제를 진행",
	)
	@PostMapping("")
	fun pay(
		@UserToken userUUID: String,
		@QueueToken tokenUUID: String,
		@RequestBody payRequest: PayRequest
	) {
		val paymentCri = payRequest.toPaymentCriCreate(userUUID, tokenUUID)
		paymentFacadeService.payReservation(paymentCri)
	}
}