package kr.hhplus.be.server.domain.alarm

import org.springframework.stereotype.Service

@Service
class AlarmService(
	private val alarmApiClient: AlarmApiClient
) {

	fun sendDataToDataPlatformFailAlarm(payload: Any) {
		val message = "데이터 플랫폼 데이터 전송 실패\npayload=$payload"
		alarmApiClient.sendAlarm(message)
	}

	fun sendDeadLetterAlarm(topic: String, payload: Any) {
		val message = "데드레터 발생\ntopic=$topic, payload=$payload"
		alarmApiClient.sendAlarm(message)
	}

	fun sendCompensationFailAlarm(aggregateType: String, aggregateId: Long, payload: Any) {
		val message = "보상 트랜잭션 실패\ntype=$aggregateType, id=$aggregateId, payload=$payload"
		alarmApiClient.sendAlarm(message)
	}
}