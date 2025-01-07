package kr.hhplus.be.server.interfaces.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.user.UserFacadeService
import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "유저")
@RestController
@RequestMapping("/users")
class UserController(
	private val userFacadeService: UserFacadeService,
	private val uuidGenerator: UuidGenerator
) {

	@Operation(
		summary = "유저 토큰 발급 요청 API",
		description = "유저 토큰을 발급",
	)
	@PostMapping("/{userId}")
	fun issueUserToken(@PathVariable userId: Long): ResponseEntity<Unit> {
		val issuedUserToken = userFacadeService.issueUserToken(userId, uuidGenerator)

		return ResponseEntity.status(HttpStatus.CREATED)
			.header(
				SET_COOKIE,
				ResponseCookie.from("user-access-token", issuedUserToken).build().toString()
			)
			.body(Unit)
	}

	@Operation(
		summary = "잔액 조회 API",
		description = "유저의 잔액 반환",
	)
	@GetMapping("/balance")
	fun getBalance(
		@CookieValue("user-access-token") userAccessToken: String?
	): RemainPointResponse {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }

		return RemainPointResponse(20000)
	}

	@Operation(
		summary = "잔액 충전 API",
		description = "유저의 잔액을 충전",
	)
	@PostMapping("/balance")
	fun chargeBalance(
		@CookieValue("user-access-token") userAccessToken: String?,
		@RequestBody chargePointRequest: ChargePointRequest
	) {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
	}
}