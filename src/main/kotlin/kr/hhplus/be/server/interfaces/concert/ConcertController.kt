package kr.hhplus.be.server.interfaces.concert

import kr.hhplus.be.server.exception.ForbiddenException
import kr.hhplus.be.server.exception.UnauthorizedException
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

@RestController
@RequestMapping("/concerts")
class ConcertController {

	@GetMapping("")
	fun getConcertInformation(
		@CookieValue("user-access-token") userAccessToken: String?,
		@CookieValue("concert-access-token") concertAccessToken: String?
	): ConcertInformationResponse {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
		require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }

		return ConcertInformationResponse(
			mutableListOf(
				ConcertInformationDto(1L, "아이유의 연말 콘서트", "아이유"),
				ConcertInformationDto(2L, "아이유의 연초 콘서트", "아이유")
			)
		)
	}

	@GetMapping("/{concertId}/schedules")
	fun getConcertSchedules(
		@CookieValue("user-access-token") userAccessToken: String?,
		@CookieValue("concert-access-token") concertAccessToken: String?,
		@PathVariable concertId: String
	): ConcertScheduleResponse {
		require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
		require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }

		val now = ZonedDateTime.now()
		return ConcertScheduleResponse(
			mutableListOf(
				ConcertScheduleDto(
					123L,
					"콘서트장1",
					"서울특별시 항해구 항해로 123번길",
					50,
					now,
					now.plusHours(3)
				),
				ConcertScheduleDto(
					124L,
					"콘서트장2",
					"서울특별시 항해구 항해로 124번길",
					50,
					now.plusDays(1),
					now.plusHours(27)
				)
			)
		)
	}

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