package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class ReservationPaymentOrchestrator(
	private val reservationService: ReservationService,
	private val userService: UserService,
	private val concertService: ConcertService,
	private val paymentService: PaymentService,
	private val queueService: QueueService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	private val flowSet = ThreadLocal<EnumSet<ReservationPaymentFlow>>()
	private val outbox = ThreadLocal<OutBox>()

	private data class OutBox(
		var userId: Long = 0L,
		var expiredAt: LocalDateTime? = null,
		var pointHistoryId: Long = 0L,
		var paymentId: Long = 0L,
		var reservationId: Long = 0L,
		var seatId: Long = 0L,
		var tokenId: Long = 0L
	)

	fun setupInitialRollbackInfo(userId: Long, expiredAt: LocalDateTime?) {
		flowSet.set(EnumSet.noneOf(ReservationPaymentFlow::class.java))
		outbox.set(OutBox())

		outbox.get().userId = userId
		outbox.get().expiredAt = expiredAt
	}

	fun successFlow(flow: ReservationPaymentFlow, id: Long) {
		flowSet.get().add(flow)

		val data = outbox.get()
		when (flow) {
			ReservationPaymentFlow.USE_POINT -> data.pointHistoryId = id
			ReservationPaymentFlow.CREATE_PAYMENT -> data.paymentId = id
			ReservationPaymentFlow.SOLD_OUT_RESERVATION -> data.reservationId = id
			ReservationPaymentFlow.SOLD_OUT_SEAT -> data.seatId = id
			ReservationPaymentFlow.DEACTIVATE_TOKEN -> data.tokenId = id
		}
	}

	fun rollbackAll() {
		flowSet.get().forEach {
			log.info("롤백 {}", it.description)

			try {
				rollbackFlow(it)
			} catch (e: Exception) {
				log.error("{} 롤백 중 에러 발생 - 스킵 :", it.description, e)
			}
		}
	}

	private fun rollbackFlow(currentFlow: ReservationPaymentFlow) {
		val data = outbox.get()
		when (currentFlow) {
			ReservationPaymentFlow.DEACTIVATE_TOKEN -> queueService.rollbackDeactivateToken(data.tokenId)
			ReservationPaymentFlow.SOLD_OUT_SEAT -> concertService.rollbackSoldOutedConcertSeat(data.seatId, data.expiredAt)
			ReservationPaymentFlow.SOLD_OUT_RESERVATION -> reservationService.rollbackReservation(data.reservationId, data.expiredAt)
			ReservationPaymentFlow.CREATE_PAYMENT -> paymentService.rollbackPayment(data.paymentId)
			ReservationPaymentFlow.USE_POINT -> userService.rollbackUsePointHistory(data.userId, data.pointHistoryId)
		}
	}

	fun clear() {
		flowSet.remove()
		outbox.remove()
	}
}

/**
 * 예약 결제는 아래의 순서로 진행된다.
 * 포인트 차감 -> 결제 생성 -> 예약 soldout -> 좌석 soldout -> 토큰 비활성화
 */
enum class ReservationPaymentFlow(val description: String) {

	USE_POINT("포인트 차감"),
	CREATE_PAYMENT("결제 생성"),
	SOLD_OUT_RESERVATION("예약 매진 처리"),
	SOLD_OUT_SEAT("좌석 매진 처리"),
	DEACTIVATE_TOKEN("토큰 비활성화"),
	;
}