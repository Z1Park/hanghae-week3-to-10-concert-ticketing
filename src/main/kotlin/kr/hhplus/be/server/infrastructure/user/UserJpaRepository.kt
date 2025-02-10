package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.infrastructure.user.entity.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {

	@EntityGraph(attributePaths = ["pointHistories"])
	fun findWithPointHistoriesById(id: Long): UserEntity?

	@EntityGraph(attributePaths = ["pointHistories"])
	fun findWithPointHistoriesByUserUUID(uuid: String): UserEntity?
}