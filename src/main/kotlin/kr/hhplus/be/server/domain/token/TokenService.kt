package kr.hhplus.be.server.domain.token

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import org.springframework.stereotype.Service
import java.time.ZoneOffset
import kotlin.math.ceil

@Service
class TokenService(
	private val tokenRepository: TokenRepository
) {

	companion object {
		private const val ACTIVATE_COUNT_PER_SEC = 25L
		private const val ACTIVE_TOKEN_TIME_TO_LIVE_SECONDS = 30L * 60 // 30분
		private const val WAITING_TOKEN_TIME_TO_LIVE_SECONDS = 60L * 60 // 60분
	}

	fun getWaitingInfo(tokenUUID: String): QueueWaitingInfo {
		val waitingRank = tokenRepository.getWaitingTokenRank(tokenUUID)
		if (waitingRank == null) {
			require(tokenRepository.isActiveTokenExist(tokenUUID)) {
				throw CustomException(ErrorCode.INVALID_QUEUE_TOKEN)
			}
			return QueueWaitingInfo(0, 0)
		}

		val expectedWaitingSeconds = ceil(waitingRank.toDouble() / ACTIVATE_COUNT_PER_SEC).toInt()
		return QueueWaitingInfo(waitingRank, expectedWaitingSeconds)
	}

	fun createWaitingToken(tokenUUID: String, clockHolder: ClockHolder) {
		val currentTime = clockHolder.getCurrentTime()
		val timeScore = currentTime.toEpochSecond(ZoneOffset.UTC) + WAITING_TOKEN_TIME_TO_LIVE_SECONDS
		return tokenRepository.createWaitingToken(tokenUUID, timeScore.toDouble())
	}

	fun removeExpiredWaitingTokens(clockHolder: ClockHolder) {
		val currentTime = clockHolder.getCurrentTime()
		val timeScore = currentTime.toEpochSecond(ZoneOffset.UTC)

		tokenRepository.removeWaitingTokenScoreRange(timeScore.toDouble())
	}

	fun activateTokens() {
		val waitingTokens = tokenRepository.getWaitingTokenRange(ACTIVATE_COUNT_PER_SEC.minus(1L))

		tokenRepository.createActiveTokens(waitingTokens, ACTIVE_TOKEN_TIME_TO_LIVE_SECONDS)

		tokenRepository.removeWaitingTokenRankRange(waitingTokens.size.toLong() - 1L)
	}

	fun validateWaitingToken(tokenUUID: String) {
		val waitingTokenExist = tokenRepository.isWaitingTokenExist(tokenUUID)
		require(waitingTokenExist) { throw CustomException(ErrorCode.INVALID_QUEUE_TOKEN) }
	}

	fun validateActiveToken(tokenUUID: String) {
		val activeTokenExist = tokenRepository.isActiveTokenExist(tokenUUID)
		require(activeTokenExist) { throw CustomException(ErrorCode.REQUIRE_ACTIVATED_QUEUE_TOKEN) }
	}
}