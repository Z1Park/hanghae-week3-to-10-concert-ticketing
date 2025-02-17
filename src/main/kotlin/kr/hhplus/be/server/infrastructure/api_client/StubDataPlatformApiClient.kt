package kr.hhplus.be.server.infrastructure.api_client

import kr.hhplus.be.server.domain.external.DataPlatformApiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StubDataPlatformApiClient : DataPlatformApiClient {
	private val log = LoggerFactory.getLogger(javaClass)

	override fun send(concertSeatId: Long, reservationId: Long) {
		log.info("send reservation info to data platform reservationId = {}", reservationId)
	}

	override fun send(concertSeatId: Long, reservationId: Long, paymentId: Long) {
		log.info("send payment info to data platform paymentId = {}", paymentId)
	}
}