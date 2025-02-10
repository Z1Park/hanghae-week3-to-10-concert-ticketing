package kr.hhplus.be.server.infrastructure.user.entity

import jakarta.persistence.*
import kr.hhplus.be.server.domain.user.model.User
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "user")
class UserEntity(
	@Column(nullable = false)
	var username: String,

	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(nullable = false, unique = true)
	var balance: Int,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userId", cascade = [CascadeType.ALL], orphanRemoval = true)
	val pointHistories: MutableList<PointHistoryEntity> = mutableListOf(),

	@Version
	var version: Long = 0L,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(user: User) : this(
		id = user.id,
		username = user.username,
		userUUID = user.userUUID,
		balance = user.balance,
		pointHistories = user.pointHistories.map { PointHistoryEntity(it) }.toMutableList()
	)

	fun toDomain(): User = User(
		id = id,
		username = username,
		userUUID = userUUID,
		balance = balance,
		pointHistories = pointHistories.map { it.toDomain() }.toMutableList()
	)
}