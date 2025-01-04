package kr.hhplus.be.server.interfaces.queue

import kr.hhplus.be.server.exception.ForbiddenException
import kr.hhplus.be.server.exception.UnauthorizedException
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/tokens")
class QueueController {

	@Operation(
		summary = "대기열 토큰 발급 API",
		description = "대기열 토큰을 발급하고 대기열에 유저를 등록",
	)
	@PostMapping("")
	fun issueQueueToken(
		@CookieValue("user-access-token") userAccessToken: String?
	): ResponseEntity<Unit> {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(
                SET_COOKIE,
                ResponseCookie.from("concert-access-token", "DH8FF4NKJD082").build().toString()
            )
            .body(Unit)
    }

    @GetMapping("")
    fun getWaitingInformation(
        @CookieValue("user-access-token") userAccessToken: String?,
        @CookieValue("concert-access-token") concertAccessToken: String?
    ): WaitingInformationResponse {
        require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
        require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }

        return WaitingInformationResponse(381297L, 25)
    }
}