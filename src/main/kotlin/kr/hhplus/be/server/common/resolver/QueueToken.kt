package kr.hhplus.be.server.common.resolver

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class QueueToken(
	val requiredType: RequiredType = RequiredType.ACTIVATED
) {

	enum class RequiredType {
		WAITING,
		ACTIVATED,
		;
	}
}
