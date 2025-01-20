package kr.hhplus.be.server.common.exception

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@Order(1)
class ApplicationExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	@ExceptionHandler(CustomException::class)
	fun handleTokenException(ex: CustomException): ResponseEntity<Any> {
		val errorCode = ex.errorCode
		val errorMessage = createErrorMessage(errorCode.message, ex.optionalMessage)

		log.warn("CustomException caused : status={}, message={}", errorCode.httpStatus, errorMessage)
		return ResponseEntity(errorMessage, errorCode.httpStatus)
	}

	@ExceptionHandler(Exception::class)
	fun handleException(ex: Exception): ResponseEntity<Any> {

		log.warn("Exception caused : status={}, message={}", HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
		return ResponseEntity(ex.message, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private fun createErrorMessage(errorCodeMessage: String, additionalMessage: String): String {
		if (additionalMessage.isNotBlank()) {
			return "$errorCodeMessage : $additionalMessage"
		}
		return errorCodeMessage
	}
}