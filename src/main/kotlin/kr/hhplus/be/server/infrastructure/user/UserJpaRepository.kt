package kr.hhplus.be.server.infrastructure.user

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface UserJpaRepository : JpaRepository<User, Long> {

	fun findByUserUUID(uuid: String): User?

	@Lock(LockModeType.OPTIMISTIC)
	fun findForUpdateByUserUUID(uuid: String): User?
}