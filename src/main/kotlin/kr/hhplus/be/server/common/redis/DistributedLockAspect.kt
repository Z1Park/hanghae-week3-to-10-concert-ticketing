package kr.hhplus.be.server.common.redis

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Order(0)
@Component
class DistributedLockAspect(
	private val redissonClient: RedissonClient
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@Around("@annotation(DistributedLock)")
	fun lockByRedisson(joinPoint: ProceedingJoinPoint): Any? {
		val signature = joinPoint.signature as MethodSignature
		val distributedLock = signature.method.getAnnotation(DistributedLock::class.java)

		val lockKey = distributedLock.prefix + parseExpressionLanguage(signature.parameterNames, joinPoint.args, distributedLock.key)
		val lock = redissonClient.getLock(lockKey)

		try {
			val available = lock.tryLock(distributedLock.starvationTime, distributedLock.lockTimeToLive, TimeUnit.MILLISECONDS)
			require(available) { return false }

			return joinPoint.proceed()
		} finally {
			runCatching { lock.unlock() }
				.onFailure { log.warn("Redis distributed lock is already unlocked : key={}", lockKey) }
				.getOrNull()
		}
	}

	fun parseExpressionLanguage(parameterNames: Array<String>, args: Array<Any>, keyEL: String): Any? {
		val parser = SpelExpressionParser()
		val context = StandardEvaluationContext()

		parameterNames.forEachIndexed { index, param -> context.setVariable(param, args[index]) }
		return parser.parseExpression(keyEL).getValue(context, Any::class)
	}
}