package kr.hhplus.be.server.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "user")
class User(
	@Column(nullable = false)
	var username: String,

	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(nullable = false, unique = true)
	var balance: Int,
) : BaseEntity() {

	fun updateUserUUID(uuid: String) {
		userUUID = uuid
	}
}