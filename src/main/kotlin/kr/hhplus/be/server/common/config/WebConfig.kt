package kr.hhplus.be.server.common.config

import kr.hhplus.be.server.common.interceptor.QueueTokenInterceptor
import kr.hhplus.be.server.common.interceptor.UserTokenInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
	private val userTokenInterceptor: UserTokenInterceptor,
	private val queueTokenInterceptor: QueueTokenInterceptor
) : WebMvcConfigurer {

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(userTokenInterceptor)
			.addPathPatterns(
				"/tokens/**",
				"/concerts/**",
				"/reservations/**",
				"/users/balance",
			)

		registry.addInterceptor(queueTokenInterceptor)
			.addPathPatterns(
				"/tokens/activate",
				"/reservations/**",
				"/concerts/**",
			)
	}
}