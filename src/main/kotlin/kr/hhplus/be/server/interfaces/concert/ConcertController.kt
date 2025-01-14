package kr.hhplus.be.server.interfaces.concert

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.concert.ConcertFacadeService
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.common.resolver.UserToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "콘서트")
@RestController
@RequestMapping("/concerts")
class ConcertController(private val concertFacadeService: ConcertFacadeService) {

	@Operation(
		summary = "콘서트 정보 조회 API",
		description = "조회된 여러 콘서트의 정보를 반환",
	)
	@GetMapping("")
	fun getConcertInformation(
		@UserToken userToken: String,
		@QueueToken queueToken: String
	): ConcertInformationResponse {
		val concertInformation = concertFacadeService.getConcertInformation()

		return ConcertInformationResponse.from(concertInformation)
	}

	@Operation(
		summary = "콘서트 일정 조회 API",
		description = "콘서트의 일정 정보를 반환",
	)
	@GetMapping("/{concertId}/schedules")
	fun getConcertSchedules(
		@UserToken userToken: String,
		@QueueToken queueToken: String,
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
		@UserToken userToken: String,
		@QueueToken queueToken: String,
		@PathVariable concertId: Long,
		@PathVariable scheduleId: Long
	): ConcertSeatResponse {
		val concertSeatInformation = concertFacadeService.getConcertSeatInformation(concertId, scheduleId)

		return ConcertSeatResponse.from(concertSeatInformation)
	}
}