package kr.hhplus.be.server.interfaces.resolver

import jakarta.servlet.http.HttpServletRequest
import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserService
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import org.slf4j.LoggerFactory
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
	private val userService: UserService
) : HandlerMethodArgumentResolver {
	private val log = LoggerFactory.getLogger(this.javaClass)!!

	override fun supportsParameter(parameter: MethodParameter): Boolean =
		parameter.hasMethodAnnotation(UserToken::class.java) && parameter.parameterType == User::class.java

	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?
	): Any? {
		val userToken = extractTokenFromCookie(webRequest) ?: throw UnauthorizedException()
		try {
			return userService.getByUuid(userToken)
		} catch (e: EntityNotFoundException) {
			log.error(e.message)
			throw UnauthorizedException()
		}
	}

	private fun extractTokenFromCookie(webRequest: NativeWebRequest): String? {
		val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
		return request?.cookies
			?.firstOrNull { it.name == "user-access-token" }
			?.value
	}
}