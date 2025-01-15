package kr.hhplus.be.server.common.exception

class CustomException(
	val errorCode: ErrorCode,
	val optionalMessage: String = ""
) : RuntimeException() {
}