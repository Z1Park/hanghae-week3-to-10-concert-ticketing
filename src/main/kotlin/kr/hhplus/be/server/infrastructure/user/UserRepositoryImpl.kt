package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
	private val userJpaRepository: UserJpaRepository
) : UserRepository {

	override fun findById(userId: Long): User? =
		userJpaRepository.findByIdOrNull(userId)

	override fun findByUuid(uuid: String): User? =
		userJpaRepository.findByUserUUID(uuid)

	override fun save(user: User): User {
		return userJpaRepository.save(user)
	}
}