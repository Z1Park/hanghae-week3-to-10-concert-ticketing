package kr.hhplus.be.server.domain.alarm

interface AlarmApiClient {

	fun sendAlarm(message: String)
}