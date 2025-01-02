package kr.hhplus.be.server.interfaces.user

import kr.hhplus.be.server.exception.UnauthorizedException
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException.Unauthorized

@RestController
@RequestMapping("/users")
class UserController {

    @PostMapping("/{userId}")
    fun publishUserToken(@PathVariable userId: Long) : ResponseEntity<Unit> {
        require(userId != 0L) { throw BadRequestException() }

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(
                SET_COOKIE,
                ResponseCookie.from("user-access-token", "EI9137BFKJD98").build().toString()
            )
            .body(Unit)
    }

    @GetMapping("/points")
    fun getRemainPoint(
        @CookieValue("user-access-token") userAccessToken: String?
    ): RemainPointResponse {
        require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }

        return RemainPointResponse(20000)
    }

    @PostMapping("/points")
    fun chargePoint(
        @CookieValue("user-access-token") userAccessToken: String?,
        @RequestBody chargePointRequest: ChargePointRequest
    ) {
        require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
    }
}