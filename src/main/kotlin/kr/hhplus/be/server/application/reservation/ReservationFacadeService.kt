package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.reservation.ReservationCommand
import kr.hhplus.be.server.domain.reservation.ReservationService
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Service

@Service
class ReservationFacadeService(
	private val reservationService: ReservationService,
	private val userService: UserService,
	private val concertService: ConcertService,
	private val clockHolder: ClockHolder
) {

	fun reserveConcertSeat(requestCri: ReservationCri.Create): ReservationResult {
		val user = userService.getByUuid(requestCri.userUUID)

		val seatTotalInfo = concertService.getConcertSeatTotalInformation(requestCri.toConcertCommandTotal())

		val createRequest = ReservationCommand.Create(
			seatTotalInfo.price,
			user.id,
			seatTotalInfo.concertId,
			seatTotalInfo.concertScheduleId,
			seatTotalInfo.seatId
		)
		val reservation = reservationService.reserve(createRequest, clockHolder)
		return ReservationResult.from(reservation)
	}
}