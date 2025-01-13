package kr.hhplus.be.server.common

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SystemClockHolder : ClockHolder {

	override fun getCurrentTime(): LocalDateTime = LocalDateTime.now()
}