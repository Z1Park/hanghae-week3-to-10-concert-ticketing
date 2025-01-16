package kr.hhplus.be.server

import kotlinx.coroutines.*
import kr.hhplus.be.server.common.exception.CustomException

object ConcurrencyTestHelper {

	fun <T, R> runRepeatedly(function: suspend (T) -> R, param: Array<T>, repeat: Int) = runBlocking {
		val context = newFixedThreadPoolContext(repeat, "동시성 테스트")

		coroutineScope {
			withContext(context) {
				for (i in 0 until repeat) {
					launch {
						try {
							supervisorScope {
								function(param[i])
							}
						} catch (e: Exception) {
							if (e is CustomException)
								println(e.errorCode.message)
						}
					}
				}
			}
		}
	}
}