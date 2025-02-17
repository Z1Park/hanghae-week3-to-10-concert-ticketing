package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.domain.user.model.User

data class UserInfo(
	val userId: Long,
	val username: String,
	val userUUID: String,
	val balance: Int
) {
	companion object {

		fun from(user: User): UserInfo =
			UserInfo(user.id, user.username, user.userUUID, user.balance)
	}
}
