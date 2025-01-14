package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Service

@Service
class UserFacadeService(
	private val userService: UserService
) {

	fun issueUserToken(userId: Long, uuidGenerator: UuidGenerator): String {
		val generatedUuid = uuidGenerator.generateUuid()
		val user = userService.saveUserUUID(userId, generatedUuid)

		return user.userUUID
	}

	fun getUserBalance(userUUID: String): Int {
		val user = userService.getByUuid(userUUID)

		return user.balance
	}

	fun charge(userUUID: String, chargeAmount: Int): Int {
		val user = userService.charge(userUUID, chargeAmount)
		return user.balance
	}
}