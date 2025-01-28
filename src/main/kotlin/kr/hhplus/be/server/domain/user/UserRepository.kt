package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.domain.user.model.User

interface UserRepository {

	fun findById(userId: Long): User?

	fun findByUuid(uuid: String): User?

	fun save(user: User): User
}