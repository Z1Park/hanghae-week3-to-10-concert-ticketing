package kr.hhplus.be.server.application.scheduler

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.payment.PaymentMessageProducer
import kr.hhplus.be.server.domain.payment.PaymentOutboxService
import kr.hhplus.be.server.domain.reservation.ReservationMessageProducer
import kr.hhplus.be.server.domain.reservation.ReservationOutboxService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class KafkaScheduler(
	private val reservationOutboxService: ReservationOutboxService,
	private val reservationMessageProducer: ReservationMessageProducer,
	private val paymentOutboxService: PaymentOutboxService,
	private val paymentMessageProducer: PaymentMessageProducer,
	private val clockHolder: ClockHolder,
) {

	@Scheduled(cron = "0 * * * * *") // 매 1분마다 실행
	fun resendUnprocessedDataToDataPlatform() {
		val unprocessedReservations = reservationOutboxService.findAllUnprocessedDataToResendDataPlatform(clockHolder.getCurrentTime())
		unprocessedReservations.forEach {
			reservationMessageProducer.sendReservationDataPlatformMessage(it)
		}

		val unprocessedPayments = paymentOutboxService.findAllUnprocessedDataToResendDataPlatform(clockHolder.getCurrentTime())
		unprocessedPayments.forEach {
			paymentMessageProducer.sendPaymentDataPlatformMessage(it)
		}
	}
}