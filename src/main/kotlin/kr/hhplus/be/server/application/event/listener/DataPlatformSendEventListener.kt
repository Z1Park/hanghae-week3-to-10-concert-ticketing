package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.DataPlatformSendPaymentEvent
import kr.hhplus.be.server.domain.external.DataPlatformApiClient
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DataPlatformSendEventListener(
	private val dataPlatformApiClient: DataPlatformApiClient
) {

	/**
	 * 결제 완료 이후 데이터 플랫폼으로 결제 내역 전송
	 */
	@Async
	@EventListener
	fun sendPaymentDataToDataPlatform(dataPlatformSendPaymentEvent: DataPlatformSendPaymentEvent) {
		dataPlatformApiClient.send(
			dataPlatformSendPaymentEvent.concertSeatId,
			dataPlatformSendPaymentEvent.reservationId,
			dataPlatformSendPaymentEvent.paymentId
		)
	}
}