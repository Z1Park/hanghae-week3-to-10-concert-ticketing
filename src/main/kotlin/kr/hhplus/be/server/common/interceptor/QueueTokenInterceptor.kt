package kr.hhplus.be.server.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.domain.token.TokenService
import kr.hhplus.be.server.domain.token.model.TokenActiveStatus
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

const val QUEUE_TOKEN_NAME = "queue-access-token"

@Component
class QueueTokenInterceptor(
	private val tokenService: TokenService
) : HandlerInterceptor {

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val queueToken = request.cookies.find { it.name == QUEUE_TOKEN_NAME }?.value
		require(!queueToken.isNullOrBlank()) { throw CustomException(ErrorCode.QUEUE_TOKEN_NOT_FOUND) }

		if (handler !is HandlerMethod) {
			return true
		}

		val requiredType = handler.getMethodAnnotation(QueueToken::class.java)!!.requiredType
		when (requiredType) {
			TokenActiveStatus.WAITING -> tokenService.validateWaitingToken(queueToken)
			TokenActiveStatus.ACTIVATED -> tokenService.validateActiveToken(queueToken)
		}

		TokenContext.setQueueToken(queueToken)
		return true
	}

	override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
		TokenContext.clearQueueToken()
	}
}