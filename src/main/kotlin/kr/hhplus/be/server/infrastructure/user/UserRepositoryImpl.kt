package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.domain.user.UserRepository
import kr.hhplus.be.server.domain.user.model.User
import kr.hhplus.be.server.infrastructure.user.entity.UserEntity
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
	private val userJpaRepository: UserJpaRepository,
	private val pointHistoryJpaRepository: PointHistoryJpaRepository
) : UserRepository {

	override fun findById(userId: Long): User? =
		userJpaRepository.findWithPointHistoriesById(userId)?.toDomain()

	override fun findByUuid(uuid: String): User? =
		userJpaRepository.findWithPointHistoriesByUserUUID(uuid)?.toDomain()

	override fun save(user: User): User =
		userJpaRepository.save(UserEntity(user)).toDomain()
}