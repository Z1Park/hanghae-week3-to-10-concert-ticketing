package kr.hhplus.be.server.common.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class LoggingFilter : OncePerRequestFilter() {

	private val log = LoggerFactory.getLogger(javaClass)

	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		val start = System.currentTimeMillis()

		try {
			filterChain.doFilter(request, response)
		} finally {
			val end = System.currentTimeMillis()
			val requestLog = "[Request] Client IP: ${request.remoteAddr} | URL: ${request.requestURL} | Method: ${request.method}"
			val responseLog = "[RESPONSE] Status: ${response.status} | Duration: ${end - start} ms"

			log.info("$requestLog --> $responseLog")
		}
	}
}