package kr.hhplus.be.server.interfaces.queue

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.queue.QueueFacadeService
import kr.hhplus.be.server.common.UuidV4Generator
import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.interfaces.exception.ForbiddenException
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import kr.hhplus.be.server.interfaces.resolver.UserToken
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "대기열")
@RestController
@RequestMapping("/tokens")
class QueueController(
	private val queueFacadeService: QueueFacadeService,
	private val uuidGenerator: UuidV4Generator
) {

	@Operation(
		summary = "대기열 토큰 발급 API",
		description = "대기열 토큰을 발급하고 대기열에 유저를 등록",
	)
	@PostMapping("")
	fun issueQueueToken(
		@UserToken tokenUser: User
	): ResponseEntity<Unit> {
		val issuedQueueToken = queueFacadeService.issueQueueToken(tokenUser.userUUID, uuidGenerator)

		return ResponseEntity.status(HttpStatus.CREATED)
			.header(
				SET_COOKIE,
				ResponseCookie.from("concert-access-token", issuedQueueToken).build().toString()
			)
			.body(Unit)
	}

	@Operation(
		summary = "대기열 순서 조회 API",
		description = "대기열 토큰을 기반으로 대기 순서 및 예상시간 반환",
	)
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