package kr.hhplus.be.server.interfaces.concert

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("/concerts")
class ConcertController {

    @GetMapping("")
    fun getConcertInformation(): ConcertInformationResponse {
        return ConcertInformationResponse(
            mutableListOf(
                ConcertInformationDto(1L, "아이유의 연말 콘서트", "아이유"),
                ConcertInformationDto(2L, "아이유의 연초 콘서트", "아이유")
            )
        )
    }

    @GetMapping("/{concertId}/schedules")
    fun getConcertSchedules(@PathVariable concertId: String): ConcertScheduleResponse {
        val now = ZonedDateTime.now()
        return ConcertScheduleResponse(
            mutableListOf(
                ConcertScheduleDto(123L, now, now.plusHours(3)),
                ConcertScheduleDto(124L, now.plusDays(1), now.plusHours(27))
            )
        )
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/sections")
    fun getConcertSections(
        @PathVariable concertId: String,
        @PathVariable scheduleId: String
    ): ConcertSectionResponse {
        return ConcertSectionResponse(
            "항해 콘서트장",
            "서울특별시 항해구 항해로 123번길",
            10000,
            mutableListOf(
                ConcertSectionDto(249L, "스탠딩석 A", 100, 83),
                ConcertSectionDto(250L, "스탠딩석 B", 100, 100),
            )
        )
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/sections/{sectionId}/seats")
    fun getConcertSeats(
        @PathVariable concertId: String,
        @PathVariable scheduleId: String,
        @PathVariable sectionId: String
    ): ConcertSeatResponse {
        return ConcertSeatResponse(
            mutableListOf(
                ConcertSeatDto(1384L, 131, 150000),
                ConcertSeatDto(1385L, 132, 150000),
            )
        )
    }
}