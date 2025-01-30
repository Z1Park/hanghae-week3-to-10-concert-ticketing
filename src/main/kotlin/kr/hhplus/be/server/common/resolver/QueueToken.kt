package kr.hhplus.be.server.common.resolver

import kr.hhplus.be.server.domain.queue.QueueActiveStatus

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueueToken(
	val requiredType: QueueActiveStatus = QueueActiveStatus.ACTIVATED
)
