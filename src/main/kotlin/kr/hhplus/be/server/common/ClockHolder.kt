package kr.hhplus.be.server.common

import java.time.LocalDateTime

fun interface ClockHolder {

	fun getCurrentTime(): LocalDateTime
}