package kr.hhplus.be.server.interfaces.resolver

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest

@Component
class TokenExtractor {

	fun extractTokenFromCookie(webRequest: NativeWebRequest, cookieName: String): String? {
		val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
		return request?.cookies
			?.firstOrNull { it.name == cookieName }
			?.value
	}
}