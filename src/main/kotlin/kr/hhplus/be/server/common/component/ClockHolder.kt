package kr.hhplus.be.server.common.component

import java.time.LocalDateTime

fun interface ClockHolder {

	fun getCurrentTime(): LocalDateTime
}