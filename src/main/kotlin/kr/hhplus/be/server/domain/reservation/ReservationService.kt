package kr.hhplus.be.server.domain.reservation

import jakarta.transaction.Transactional
import kr.hhplus.be.server.application.concert.ConcertApiClient
import kr.hhplus.be.server.application.event.ReservationFailEvent
import kr.hhplus.be.server.application.event.ReservationSuccessEvent
import kr.hhplus.be.server.application.user.UserApiClient
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.redis.DistributedLock
import kr.hhplus.be.server.domain.reservation.model.Reservation
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReservationService(
	private val reservationRepository: ReservationRepository,
	private val concertApiClient: ConcertApiClient,
	private val userApiClient: UserApiClient,
	private val applicationEventPublisher: ApplicationEventPublisher
) {
	private val log = LoggerFactory.getLogger(javaClass)

	companion object {
		private const val RESERVATION_KEY = "reservation:"
		private const val CACHE_SIZE = 20L
	}

	fun getReservationForPay(reservationId: Long, clockHolder: ClockHolder): Reservation {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		if (reservation.isExpired(clockHolder.getCurrentTime())) {
			throw CustomException(ErrorCode.EXPIRED_RESERVATION)
		}

		return reservation
	}

	fun getYesterdayReservationConcertCounts(clockHolder: ClockHolder): List<Long> {
		val end = clockHolder.getCurrentTime().toLocalDate().atStartOfDay()
		val start = end.minusDays(1)

		return reservationRepository.findTopReservationConcertIdsByCreatedAtBetween(start, end, CACHE_SIZE)
	}

	@Transactional
	fun reserve(command: ReservationCommand.Create, clockHolder: ClockHolder) {
		val concertSeatReservationInfo = concertApiClient.concertApiPreoccupyConcert(command.toPreoccupyCommand())

		try {
			val userInfo = userApiClient.userApiGetUserByUUID(command.userUUID)

			val seatReservation = reservationRepository.findByScheduleIdAndSeatId(command.concertScheduleId, command.concertSeatId)

			if (seatReservation != null) {
				val currentTime = clockHolder.getCurrentTime()
				require(seatReservation.isExpired(currentTime)) { throw CustomException(ErrorCode.ALREADY_RESERVED) }

				reservationRepository.delete(seatReservation)
			}

			val reservation = Reservation(
				concertSeatReservationInfo.expiredAt,
				concertSeatReservationInfo.price,
				userInfo.userId,
				concertSeatReservationInfo.concertId,
				concertSeatReservationInfo.concertScheduleId,
				concertSeatReservationInfo.concertSeatId
			)
			reservationRepository.save(reservation)

			applicationEventPublisher.publishEvent(
				ReservationSuccessEvent(
					concertSeatReservationInfo.concertSeatId,
					reservation.id
				)
			)
		} catch (e: Exception) {
			log.error("예약 실패 및 롤백 시퀀스 실행 : ", e)
			applicationEventPublisher.publishEvent(
				ReservationFailEvent(
					concertSeatReservationInfo.concertSeatId,
					concertSeatReservationInfo.originExpiredAt
				)
			)
		}
	}

	@DistributedLock(prefix = RESERVATION_KEY, key = "#reservationId")
	@Transactional
	fun makeSoldOut(reservationId: Long) {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		reservation.soldOut()
		reservationRepository.save(reservation)
	}

	@DistributedLock(prefix = RESERVATION_KEY, key = "#reservationId")
	@Transactional
	fun rollbackReservation(reservationId: Long, expiredAt: LocalDateTime?) {
		val reservation = reservationRepository.findById(reservationId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "reservationId=$reservationId")

		reservation.rollbackSoldOut(expiredAt)
		reservationRepository.save(reservation)
	}
}