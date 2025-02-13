package kr.hhplus.be.server.infrastructure.api_client

import kr.hhplus.be.server.domain.external.DataPlatformApiClient
import org.springframework.stereotype.Component

@Component
class StubDataPlatformApiClient : DataPlatformApiClient {

	override fun send(concertSeatId: Long, reservationId: Long, paymentId: Long) {
		// do something
	}
}