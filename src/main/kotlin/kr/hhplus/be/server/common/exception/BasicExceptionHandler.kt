package kr.hhplus.be.server.common.exception

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestValueException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
@Order(0)
class BasicExceptionHandler {

	private val log = LoggerFactory.getLogger(javaClass)

	companion object {
		const val EXCEPTION_MESSAGE = "{} caused : status={}, message={}"
	}

	fun logExceptionMessage(exceptionName: String, status: HttpStatus, message: String?) =
		log.warn(EXCEPTION_MESSAGE, exceptionName, status, message)

	// PathVariable, RequestParam, RequestBody 등의 값 누락 시
	@ExceptionHandler(MissingRequestValueException::class)
	fun handleMissingRequestValueException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: MissingRequestValueException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Missing value ${e.message}"

		logExceptionMessage("MissingRequestValueException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// MultiPart(RequestPart) 누락 시
	@ExceptionHandler(MissingServletRequestPartException::class)
	fun handleMissingServletRequestPartException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: MissingServletRequestPartException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Missing RequestPart ${e.requestPartName}"

		logExceptionMessage("MethodArgumentNotValidException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// argument 검증 오류 시
	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: MethodArgumentNotValidException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Missing RequestParam ${e.parameter}"

		logExceptionMessage("MethodArgumentNotValidException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// PathVariable, RequestParam, RequestBody 변환, 검증 실패 혹은 영속화 제약사항 위반 시
	@ExceptionHandler(ConstraintViolationException::class)
	protected fun handlerConstraintViolationException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: ConstraintViolationException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Validation fail ${e.message}"

		logExceptionMessage("ConstraintViolationException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// argument 타입이 맞지 않는 경우
	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: MethodArgumentTypeMismatchException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Mismatched type ${e.parameter}"

		logExceptionMessage("MethodArgumentTypeMismatchException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// RequestBody 포맷 오류
	@ExceptionHandler(HttpMessageNotReadableException::class)
	protected fun handlerHttpMessageNotReadableException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: HttpMessageNotReadableException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "Fail to read HTTP body - ${e.httpInputMessage}"

		logExceptionMessage("HttpMessageNotReadableException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// 지원하지 않는 HTTP 메서드 호출 시
	@ExceptionHandler(HttpRequestMethodNotSupportedException::class)
	fun handleHttpRequestMethodNotSupportedException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: HttpRequestMethodNotSupportedException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = "HTTP method not supported for ${e.method}"

		logExceptionMessage("HttpRequestMethodNotSupportedException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}

	// 잘못된 경로 요청 시, 리소스를 찾을 수 없는 경우
	@ExceptionHandler(NoResourceFoundException::class)
	fun handleNoResourceFoundException(
		request: HttpServletRequest?,
		response: HttpServletResponse,
		e: NoResourceFoundException
	): ResponseEntity<Any> {
		val errorStatus = HttpStatus.BAD_REQUEST
		val message = e.message

		logExceptionMessage("NoResourceFoundException", errorStatus, message)

		return ResponseEntity(message, errorStatus)
	}
}