package kr.hhplus.be.server.common.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.hhplus.be.server.common.component.TokenContext
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

const val USER_TOKEN_NAME = "user-access-token"

@Component
class UserTokenInterceptor(
	private val userService: UserService
) : HandlerInterceptor {

	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val userToken = request.cookies.find { it.name == USER_TOKEN_NAME }?.value
		require(!userToken.isNullOrBlank()) { throw CustomException(ErrorCode.USER_TOKEN_NOT_FOUND) }

		val userUUID = runCatching {
			userService.getByUuid(userToken).userUUID
		}.onFailure {
			throw CustomException(ErrorCode.INVALID_USER_TOKEN, "userToken=$userToken")
		}.getOrThrow()

		TokenContext.setUserToken(userUUID)

		return true
	}

	override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
		TokenContext.clearUserToken()
	}
}