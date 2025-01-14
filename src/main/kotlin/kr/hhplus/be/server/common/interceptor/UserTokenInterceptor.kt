package kr.hhplus.be.server.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.domain.user.UserService
import kr.hhplus.be.server.interfaces.exception.UnauthorizedException
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class UserTokenInterceptor(
	private val userService: UserService
) : HandlerInterceptor {

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		return runCatching {
			val userToken = request.cookies.find { it.name == "" }!!.value

			val userUUID = userService.getByUuid(userToken).userUUID
			TokenContext.setUserToken(userUUID)

			true
		}.getOrElse { throw UnauthorizedException() }
	}

	override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
		TokenContext.clearUserToken()
	}
}