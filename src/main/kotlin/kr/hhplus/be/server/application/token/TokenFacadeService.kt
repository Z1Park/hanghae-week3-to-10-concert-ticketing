package kr.hhplus.be.server.application.token

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.domain.token.QueueWaitingInfo
import kr.hhplus.be.server.domain.token.TokenService
import org.springframework.stereotype.Service

@Service
class TokenFacadeService(
	private val tokenService: TokenService,
	private val clockHolder: ClockHolder
) {

	fun issueQueueToken(userUUID: String, uuidGenerator: UuidGenerator): String {
		val generatedUuid = uuidGenerator.generateUuid()

		tokenService.createWaitingToken(generatedUuid, clockHolder)
		return generatedUuid
	}

	fun getWaitingInfo(tokenUUID: String): QueueWaitingInfo =
		tokenService.getWaitingInfo(tokenUUID)

	fun refreshTokens() {
		tokenService.removeExpiredWaitingTokens(clockHolder)

		tokenService.activateTokens()
	}
}