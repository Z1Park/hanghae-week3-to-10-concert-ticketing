package kr.hhplus.be.server.common.resolver

import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.domain.queue.model.Queue
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val QUEUE_TOKEN_COOKIE_NAME = "queue-access-token"

@Component
class QueueArgumentResolver : HandlerMethodArgumentResolver {

	override fun supportsParameter(parameter: MethodParameter): Boolean =
		parameter.hasMethodAnnotation(QueueToken::class.java) && parameter.parameterType == Queue::class.java

	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?
	): Any? {
		return TokenContext.getQueueToken()
	}
}