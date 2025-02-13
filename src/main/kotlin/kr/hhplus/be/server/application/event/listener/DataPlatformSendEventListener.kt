package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.DataPlatformSendEvent
import kr.hhplus.be.server.domain.external.DataPlatformApiClient
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DataPlatformSendEventListener(
	private val dataPlatformApiClient: DataPlatformApiClient
) {

	/**
	 * 결제 완료 이후(AFTER_COMMIT), 데이터 플랫폼으로 결제 내역 전송
	 */
	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	fun sendDataToDataPlatform(dataPlatformSendEvent: DataPlatformSendEvent) {
		dataPlatformApiClient.send(
			dataPlatformSendEvent.concertSeatId,
			dataPlatformSendEvent.reservationId,
			dataPlatformSendEvent.paymentId
		)
	}
}