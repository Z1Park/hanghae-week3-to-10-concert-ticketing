package kr.hhplus.be.server.common.resolver

import kr.hhplus.be.server.domain.token.model.TokenActiveStatus

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueueToken(
	val requiredType: TokenActiveStatus = TokenActiveStatus.ACTIVATED
)
