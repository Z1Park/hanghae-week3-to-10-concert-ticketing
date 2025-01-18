package kr.hhplus.be.server.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.domain.queue.QueueService
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

const val QUEUE_TOKEN_NAME = "queue-access-token"

@Component
class QueueTokenInterceptor(
	private val queueService: QueueService
) : HandlerInterceptor {

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val queueToken = request.cookies.find { it.name == QUEUE_TOKEN_NAME }?.value
		require(!queueToken.isNullOrBlank()) { throw CustomException(ErrorCode.QUEUE_TOKEN_NOT_FOUND) }

		if (handler !is HandlerMethod) {
			return true
		}

		val queue = runCatching {
			queueService.getByUuid(queueToken)
		}.onFailure {
			throw CustomException(ErrorCode.INVALID_QUEUE_TOKEN, "queueToken=$queueToken")
		}.getOrThrow()

		TokenContext.setQueueToken(queue.tokenUUID)

		val requiredType = handler.getMethodAnnotation(QueueToken::class.java)!!.requiredType
		if (requiredType == QueueToken.RequiredType.ACTIVATED) {
			require(queue.isActivated()) { throw CustomException(ErrorCode.REQUIRE_ACTIVATED_QUEUE_TOKEN) }
		}

		return true
	}

	override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
		TokenContext.clearQueueToken()
	}
}