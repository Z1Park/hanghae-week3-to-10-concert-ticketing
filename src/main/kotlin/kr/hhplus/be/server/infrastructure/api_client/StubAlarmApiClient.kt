package kr.hhplus.be.server.infrastructure.api_client

import kr.hhplus.be.server.domain.alarm.AlarmApiClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StubAlarmApiClient : AlarmApiClient {
	private val log = LoggerFactory.getLogger(javaClass)

	/**
	 * 슬랙, 구글챗, 디코 등 개발자에게 DLT 발생 알럿
	 */
	override fun sendAlarm(message: String) {
		log.error(message)
	}
}