package kr.hhplus.be.server.domain.user

import jakarta.transaction.Transactional
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
	private val userRepository: UserRepository
) {

	fun getByUuid(uuid: String): User = userRepository.findByUuid(uuid)
		?: throw EntityNotFoundException.fromParam("User", "uuid", uuid)

	@Transactional
	fun saveUserUUID(userId: Long, uuid: String): User {
		val user = userRepository.findById(userId)
			?: throw EntityNotFoundException.fromId("User", userId)

		user.updateUserUUID(uuid)
		return userRepository.save(user)
	}

	@Transactional
	fun charge(userUUID: String, chargeAmount: Int): User {
		val user = userRepository.findByUuidForUpdate(userUUID)
			?: throw EntityNotFoundException.fromParam("User", "uuid", userUUID)

		val chargePointHistory = user.charge(chargeAmount)
		userRepository.save(chargePointHistory)
		return userRepository.save(user)
	}

	@Transactional
	fun use(userUUID: String, useAmount: Int): User {
		val user = userRepository.findByUuidForUpdate(userUUID)
			?: throw EntityNotFoundException.fromParam("User", "uuid", userUUID)

		val usePointHistory = user.use(useAmount)
		userRepository.save(usePointHistory)
		return userRepository.save(user)
	}
}