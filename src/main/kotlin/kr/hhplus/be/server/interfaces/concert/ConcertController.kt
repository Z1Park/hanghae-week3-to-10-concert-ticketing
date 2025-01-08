package kr.hhplus.be.server.interfaces.concert

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.concert.ConcertFacadeService
import kr.hhplus.be.server.interfaces.exception.ForbiddenException
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import kr.hhplus.be.server.interfaces.resolver.QueueToken
import kr.hhplus.be.server.interfaces.resolver.UserToken
import org.springframework.web.bind.annotation.*

@Tag(name = "콘서트")
@RestController
@RequestMapping("/concerts")
class ConcertController(private val concertFacadeService: ConcertFacadeService) {

	@Operation(
		summary = "콘서트 정보 조회 API",
		description = "조회된 여러 콘서트의 정보를 반환",
	)
	@GetMapping("")
	fun getConcertInformation(@UserToken userToken: String, @QueueToken queueToken: String): ConcertInformationResponse {
		val concertInformation = concertFacadeService.getConcertInformation()

		return ConcertInformationResponse.from(concertInformation)
	}

	@Operation(
		summary = "콘서트 일정 조회 API",
		description = "콘서트의 일정 정보를 반환",
	)
	@GetMapping("/{concertId}/schedules")
	fun getConcertSchedules(
		@UserToken userUUID: String,
		@QueueToken tokenUUID: String,
		@PathVariable concertId: Long
	): ConcertScheduleResponse {
		val concertScheduleInformation = concertFacadeService.getConcertScheduleInformation(concertId)

		return ConcertScheduleResponse.from(concertScheduleInformation)
	}

	@Operation(
		summary = "콘서트 좌석 조회 API",
		description = "콘서트의 좌석 정보를 반환",
	)
	@GetMapping("/{concertId}/schedules/{scheduleId}/seats")
	fun getConcertSeats(
		@CookieValue("user-access-token") userAccessToken: String?,
		@CookieValue("concert-access-token") concertAccessToken: String?,
		@PathVariable concertId: String,
		@PathVariable scheduleId: String
	): ConcertSeatResponse {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
		require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }

		return ConcertSeatResponse(
			mutableListOf(
				ConcertSeatDto(1384L, 131, 150000),
				ConcertSeatDto(1385L, 132, 150000),
			)
		)
	}
}