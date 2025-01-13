package kr.hhplus.be.server.interfaces.resolver

import kr.hhplus.be.server.domain.queue.Queue
import kr.hhplus.be.server.domain.queue.QueueService
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import kr.hhplus.be.server.interfaces.exception.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val QUEUE_TOKEN_COOKIE_NAME = "queue-access-token"

@Component
class QueueArgumentResolver(
	private val queueService: QueueService,
	private val tokenExtractor: TokenExtractor
) : HandlerMethodArgumentResolver {
	private val log = LoggerFactory.getLogger(this.javaClass)!!

	override fun supportsParameter(parameter: MethodParameter): Boolean =
		parameter.hasMethodAnnotation(QueueToken::class.java) && parameter.parameterType == Queue::class.java

	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?
	): Any? {
		val queueToken = tokenExtractor.extractTokenFromCookie(webRequest, QUEUE_TOKEN_COOKIE_NAME)
			?: throw ForbiddenException()

		try {
			val queue = queueService.getByUuid(queueToken)
			require(queue.isActivated()) { throw ForbiddenException() }

			return queue.tokenUUID
		} catch (e: EntityNotFoundException) {
			log.error(e.message)
			throw ForbiddenException()
		}
	}
}