package kr.hhplus.be.server.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(CustomException::class)
	fun handleTokenException(ex: CustomException): ResponseEntity<Any> {
		val errorCode = ex.errorCode
		val errorMessage = createErrorMessage(errorCode.message, ex.optionalMessage)

		return ResponseEntity(errorMessage, errorCode.httpStatus)
	}

	private fun createErrorMessage(errorCodeMessage: String, additionalMessage: String): String {
		if (additionalMessage.isNotBlank()) {
			return "$errorCodeMessage : $additionalMessage"
		}
		return errorCodeMessage
	}
}