package kr.hhplus.be.server.interfaces.payment

import kr.hhplus.be.server.exception.ForbiddenException
import kr.hhplus.be.server.exception.UnauthorizedException
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.Forbidden

@RestController
@RequestMapping("/payments")
class PaymentController {

    @PostMapping("")
    fun pay(
        @CookieValue("user-access-token") userAccessToken: String?,
        @RequestBody payRequest: PayRequest
    ) {
        require(!payRequest.reservationIds.contains(0)) { throw BadRequestException() }
        require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
        require(!payRequest.reservationIds.contains(-1)) { throw ForbiddenException() }
    }
}