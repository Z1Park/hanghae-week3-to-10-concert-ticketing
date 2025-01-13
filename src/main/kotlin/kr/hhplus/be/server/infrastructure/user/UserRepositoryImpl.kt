package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.domain.user.PointHistory
import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
	private val userJpaRepository: UserJpaRepository,
	private val pointHistoryJpaRepository: PointHistoryJpaRepository
) : UserRepository {

	override fun findById(userId: Long): User? = userJpaRepository.findByIdOrNull(userId)

	override fun findByUuid(uuid: String): User? = userJpaRepository.findByUserUUID(uuid)

	override fun findByUuidForUpdate(uuid: String): User? = userJpaRepository.findForUpdateByUserUUID(uuid)

	override fun save(user: User): User = userJpaRepository.save(user)

	override fun save(pointHistory: PointHistory): PointHistory = pointHistoryJpaRepository.save(pointHistory)
}