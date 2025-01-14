package kr.hhplus.be.server.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.common.resolver.QueueToken
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.interfaces.exception.ForbiddenException
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class QueueTokenInterceptor(
	private val queueService: QueueService
) : HandlerInterceptor {

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		return runCatching {
			val queueToken = request.cookies.find { it.name == "" }!!.value

			if (handler !is HandlerMethod) {
				return true
			}

			val queue = queueService.getByUuid(queueToken)
			TokenContext.setQueueToken(queue.tokenUUID)

			val requiredType = handler.getMethodAnnotation(QueueToken::class.java)!!.requiredType
			if (requiredType == QueueToken.RequiredType.ACTIVATED) {
				require(queue.isActivated()) { throw BadRequestException() }
			}

			true
		}.getOrElse { throw ForbiddenException() }
	}

	override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
		TokenContext.clearQueueToken()
	}
}