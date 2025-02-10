package kr.hhplus.be.server.infrastructure.token

import kr.hhplus.be.server.domain.token.TokenRepository
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class TokenRepositoryImpl(
	private val zSetOperations: ZSetOperations<String, String>,
	private val valueOperations: ValueOperations<String, String>
) : TokenRepository {

	companion object {
		private const val WAITING_TOKEN_KEY = "waitingToken"
		private const val ACTIVE_TOKEN_VALUE = "activated"
		private val TIME_UNIT = TimeUnit.SECONDS
	}

	override fun isWaitingTokenExist(tokenUUID: String): Boolean =
		zSetOperations.score(WAITING_TOKEN_KEY, tokenUUID) != null

	override fun isActiveTokenExist(tokenUUID: String): Boolean =
		valueOperations[tokenUUID] != null

	override fun getWaitingTokenRange(activateTokenCount: Long): List<String> =
		zSetOperations.range(WAITING_TOKEN_KEY, 0, activateTokenCount)!!.toList()

	override fun getWaitingTokenRank(tokenUUID: String): Long? =
		zSetOperations.rank(WAITING_TOKEN_KEY, tokenUUID)!!

	override fun createWaitingToken(tokenUUID: String, score: Double) {
		zSetOperations.add(WAITING_TOKEN_KEY, tokenUUID, score)
	}

	override fun createActiveTokens(tokenUUIDs: List<String>, timeoutSeconds: Long) {
		val tokens = tokenUUIDs.associateWith { ACTIVE_TOKEN_VALUE }
		valueOperations.multiSet(tokens)
	}

	override fun removeWaitingToken(tokenUUID: String) {
		zSetOperations.remove(WAITING_TOKEN_KEY, tokenUUID)
	}

	override fun removeWaitingTokenRankRange(rankRange: Long) {
		zSetOperations.removeRange(WAITING_TOKEN_KEY, 0, rankRange)
	}

	override fun removeWaitingTokenScoreRange(score: Double) {
		zSetOperations.removeRangeByScore(WAITING_TOKEN_KEY, 0.0, score)
	}
}