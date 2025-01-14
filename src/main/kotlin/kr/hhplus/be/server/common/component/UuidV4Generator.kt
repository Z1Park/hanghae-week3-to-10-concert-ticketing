package kr.hhplus.be.server.common.component

import org.springframework.stereotype.Component
import java.util.*

@Component
class UuidV4Generator : UuidGenerator {

	override fun generateUuid(): String {
		return UUID.randomUUID().toString()
	}
}