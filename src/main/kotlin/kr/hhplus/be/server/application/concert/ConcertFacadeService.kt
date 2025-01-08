package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.ConcertInfo
import kr.hhplus.be.server.domain.concert.ConcertService
import org.springframework.stereotype.Service

@Service
class ConcertFacadeService(
	private val concertService: ConcertService
) {

	fun getConcertInformation(): List<ConcertInfo.Concert> =
		concertService.getConcertInformation()

	fun getConcertScheduleInformation(concertId: Long): List<ConcertInfo.Schedule> =
		concertService.getConcertSchedule(concertId)
}