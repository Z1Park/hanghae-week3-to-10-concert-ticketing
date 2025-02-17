package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertCacheService
import kr.hhplus.be.server.domain.concert.ConcertInfo
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.reservation.ReservationService
import org.springframework.stereotype.Service

@Service
class ConcertFacadeService(
	private val concertService: ConcertService,
	private val concertCacheService: ConcertCacheService,
	private val reservationService: ReservationService,
	private val clockHolder: ClockHolder
) {

	fun getConcertInformation(): List<ConcertInfo.ConcertDto> =
		concertService.getConcert()

	fun getConcertScheduleInformation(concertId: Long): List<ConcertInfo.Schedule> =
		concertService.getConcertSchedule(concertId)

	fun getConcertSeatInformation(concertId: Long, concertScheduleId: Long): List<ConcertInfo.Seat> =
		concertService.getConcertSeat(concertId, concertScheduleId)

	fun getYesterdayTopConcertInfo(): List<ConcertInfo.ConcertDto>? {
		val topConcertInfos = concertCacheService.getTopConcertInfo()

		if (topConcertInfos == null) {
			updateYesterdayTopConcertInfo()
			return concertCacheService.getTopConcertInfo()
		}

		return topConcertInfos
	}

	fun updateYesterdayTopConcertInfo() {
		val topConcertIds = reservationService.getYesterdayReservationConcertCounts(clockHolder)
		val topConcertInfos = concertService.getConcertInfos(topConcertIds)
		concertCacheService.saveTopConcertInfo(topConcertInfos, clockHolder)
	}
}