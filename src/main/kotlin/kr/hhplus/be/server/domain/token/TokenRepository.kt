package kr.hhplus.be.server.domain.token

interface TokenRepository {

	fun isWaitingTokenExist(tokenUUID: String): Boolean

	fun isActiveTokenExist(tokenUUID: String): Boolean

	fun getWaitingTokenRange(activateTokenCount: Long): List<String>

	fun getWaitingTokenRank(tokenUUID: String): Long?

	fun createWaitingToken(tokenUUID: String, score: Double)

	fun createActiveTokens(tokenUUIDs: List<String>, timeoutSeconds: Long)

	fun removeWaitingToken(tokenUUID: String)

	fun removeWaitingTokenRankRange(rankRange: Long)

	fun removeWaitingTokenScoreRange(score: Double)
}