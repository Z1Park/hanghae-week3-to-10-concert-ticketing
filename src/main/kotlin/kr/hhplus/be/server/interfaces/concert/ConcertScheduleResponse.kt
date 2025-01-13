package kr.hhplus.be.server.interfaces.concert

import kr.hhplus.be.server.domain.concert.ConcertInfo
import java.time.LocalDateTime

data class ConcertScheduleResponse(
	val concertSchedules: List<ConcertScheduleDto>
) {
	companion object {
		fun from(scheduleInformation: List<ConcertInfo.Schedule>): ConcertScheduleResponse =
			ConcertScheduleResponse(scheduleInformation.map { ConcertScheduleDto.from(it) })
	}
}

data class ConcertScheduleDto(
	val concertId: Long,
	val concertScheduleId: Long,
	val totalSeat: Int,
	val startAt: LocalDateTime,
	val endAt: LocalDateTime
) {
	companion object {
		fun from(scheduleInformation: ConcertInfo.Schedule): ConcertScheduleDto =
			ConcertScheduleDto(
				scheduleInformation.concertId,
				scheduleInformation.concertScheduleId,
				scheduleInformation.totalSeat,
				scheduleInformation.startAt,
				scheduleInformation.endAt
			)
	}
}
