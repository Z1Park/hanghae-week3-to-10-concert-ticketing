package kr.hhplus.be.server.domain.user

interface UserRepository {

	fun findById(userId: Long): User?

	fun findByUuid(uuid: String): User?

	fun save(user: User): User
}