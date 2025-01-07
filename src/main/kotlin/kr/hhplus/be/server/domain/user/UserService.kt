package kr.hhplus.be.server.domain.user

import jakarta.transaction.Transactional
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
	private val userRepository: UserRepository
) {

	fun getById(userId: Long): User = userRepository.findById(userId)
		?: throw EntityNotFoundException.fromId("User", userId)

	fun getByUuid(uuid: String): User = userRepository.findByUuid(uuid)
		?: throw EntityNotFoundException.fromParam("User", "uuid", uuid)

	@Transactional
	fun updateUserUuid(user: User, uuid: String) {
		user.updateUserUUID(uuid)
		userRepository.save(user)
	}
}