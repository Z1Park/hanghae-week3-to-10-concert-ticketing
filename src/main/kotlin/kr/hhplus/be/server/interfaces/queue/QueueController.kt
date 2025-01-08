package kr.hhplus.be.server.interfaces.queue

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.queue.QueueFacadeService
import kr.hhplus.be.server.common.UuidV4Generator
import kr.hhplus.be.server.interfaces.resolver.QueueToken
import kr.hhplus.be.server.interfaces.resolver.UserToken
import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
	fun issueQueueToken(@UserToken userToken: String): ResponseEntity<Unit> {
		val issuedQueueToken = queueFacadeService.issueQueueToken(userToken, uuidGenerator)

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
		@UserToken userToken: String,
		@QueueToken queueToken: String
	): WaitingInformationResponse {
		val waitingInfo = queueFacadeService.getWaitingInfo(queueToken)
		return WaitingInformationResponse.from(waitingInfo)
	}

	@Operation(
		summary = "대기열 토큰 상태 변경 스케줄러 API",
		description = "배치 혹은 스케줄러에 의해 호출되어, 대기열 토큰 중 일부를 만료 혹은 활성 상태로 변경"
	)
	@PostMapping("/activate")
	fun activateToken() {
		queueFacadeService.refreshTokens()
	}
}