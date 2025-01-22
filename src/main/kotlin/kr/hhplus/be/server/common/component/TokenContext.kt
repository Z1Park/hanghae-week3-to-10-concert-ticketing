package kr.hhplus.be.server.common.component

object TokenContext {

	private val userTokenContext = ThreadLocal<String>()
	private val queueTokenContext = ThreadLocal<String>()

	fun setUserToken(userToken: String) = userTokenContext.set(userToken)

	fun setQueueToken(queueToken: String) = queueTokenContext.set(queueToken)

	fun getUserToken(): String = userTokenContext.get()

	fun getQueueToken(): String = queueTokenContext.get()

	fun clearUserToken() = userTokenContext.remove()

	fun clearQueueToken() = queueTokenContext.remove()
}