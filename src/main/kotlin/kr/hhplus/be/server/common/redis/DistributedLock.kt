package kr.hhplus.be.server.common.redis

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(

	val prefix: String,

	val key: String,

	val starvationTime: Long = 5000L,

	val lockTimeToLive: Long = 3000L
)
