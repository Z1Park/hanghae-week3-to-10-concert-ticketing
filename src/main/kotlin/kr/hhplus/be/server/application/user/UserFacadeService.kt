package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.domain.user.UserService
import org.springframework.stereotype.Service

@Service
class UserFacadeService(
	private val userService: UserService
) {

	fun issueUserToken(userId: Long, uuidGenerator: UuidGenerator): String {
		val user = userService.getById(userId)

		val generatedUuid = uuidGenerator.generateUuid()
		userService.updateUserUuid(user, generatedUuid)

		return generatedUuid
	}

	fun getUserBalance(userUUID: String): Int {
		val user = userService.getByUuid(userUUID)

		return user.balance
	}
}