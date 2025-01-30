package kr.hhplus.be.server.common.exception

import kr.hhplus.be.server.common.exception.BasicExceptionHandler.Companion.EXCEPTION_MESSAGE
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@Order(1)
class GlobalExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	fun logExceptionMessage(exceptionName: String, status: HttpStatus, message: String?) =
		log.warn(EXCEPTION_MESSAGE, exceptionName, status, message)

	@ExceptionHandler(CustomException::class)
	fun handleTokenException(ex: CustomException): ResponseEntity<Any> {
		val errorCode = ex.errorCode
		val errorMessage = createErrorMessage(errorCode.message, ex.optionalMessage)

		logExceptionMessage("CustomException", errorCode.httpStatus, errorMessage)
		return ResponseEntity(errorMessage, errorCode.httpStatus)
	}

	@ExceptionHandler(OptimisticLockingFailureException::class)
	fun handleObjectOptimisticLockingFailureException(ex: ObjectOptimisticLockingFailureException): ResponseEntity<Any> {
		val status = HttpStatus.BAD_REQUEST
		val message = "Primary Key=${ex.identifier}"

		logExceptionMessage("OptimisticLockingFailureException", status, message)
		return ResponseEntity(message, status)
	}

	@ExceptionHandler(Exception::class)
	fun handleException(ex: Exception): ResponseEntity<Any> {
		val status = HttpStatus.INTERNAL_SERVER_ERROR
		val message = ex.message

		logExceptionMessage("Exception", status, message)
		return ResponseEntity(message, status);
	}

	private fun createErrorMessage(errorCodeMessage: String, additionalMessage: String): String {
		if (additionalMessage.isNotBlank()) {
			return "$errorCodeMessage : $additionalMessage"
		}
		return errorCodeMessage
	}
}