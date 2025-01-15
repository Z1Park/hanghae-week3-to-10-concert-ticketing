package kr.hhplus.be.server.common.resolver

import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.domain.user.User
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val USER_TOKEN_COOKIE_NAME = "user-access-token"

@Component
class UserArgumentResolver : HandlerMethodArgumentResolver {

	override fun supportsParameter(parameter: MethodParameter): Boolean =
		parameter.hasMethodAnnotation(UserToken::class.java) && parameter.parameterType == User::class.java

	override fun resolveArgument(
		parameter: MethodParameter,
		mavContainer: ModelAndViewContainer?,
		webRequest: NativeWebRequest,
		binderFactory: WebDataBinderFactory?
	): Any? {
		return TokenContext.getUserToken()
	}
}