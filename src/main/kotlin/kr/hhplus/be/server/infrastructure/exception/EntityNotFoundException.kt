package kr.hhplus.be.server.infrastructure.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EntityNotFoundException(message: String) : RuntimeException(message) {

	companion object {
		private val MESSAGE_FORMAT = "%s 엔티티를 찾을 수 없습니다. %s=%s"

		fun fromId(domainName: String, id: Long): EntityNotFoundException {
			return EntityNotFoundException(MESSAGE_FORMAT.format(domainName, "Id", id))
		}

		fun fromParam(domainName: String, paramName: String, param: String): EntityNotFoundException {
			return EntityNotFoundException(MESSAGE_FORMAT.format(domainName, paramName, param))
		}
	}
}