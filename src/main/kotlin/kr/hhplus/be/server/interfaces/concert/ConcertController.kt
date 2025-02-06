package kr.hhplus.be.server.interfaces.concert

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.hhplus.be.server.application.concert.ConcertFacadeService
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.common.resolver.UserToken
import kr.hhplus.be.server.interfaces.concert.dto.ConcertInformationResponse
import kr.hhplus.be.server.interfaces.concert.dto.ConcertScheduleResponse
import kr.hhplus.be.server.interfaces.concert.dto.ConcertSeatResponse
import kr.hhplus.be.server.interfaces.concert.dto.TopConcertInformationResponse
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

	@Operation(
		summary = "일간 인기 콘서트 캐시 갱신 요청 API",
		description = "호출 시 일간 인기 콘서트 정보를 캐시에 갱신하며, 외부 배치에서 매일 00:00:00에 부른다",
	)
	@PostMapping("/daily")
	fun updateTopConcerts() {
		concertFacadeService.updateYesterdayTopConcertInfo()
	}

	@Operation(
		summary = "일간 인기 콘서트 조회 API",
		description = "일간 인기 콘서트 정보를 조회한다.",
	)
	@GetMapping("/daily")
	fun getTopConcerts(): TopConcertInformationResponse {
		val topConcerts = concertFacadeService.getYesterdayTopConcertInfo()

		return TopConcertInformationResponse.from(topConcerts)
	}
}