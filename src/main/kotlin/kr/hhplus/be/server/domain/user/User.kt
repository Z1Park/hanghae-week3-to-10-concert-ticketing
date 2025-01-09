package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity
import org.apache.coyote.BadRequestException

@Entity
@Table(name = "user")
class User(
	@Column(nullable = false)
	var username: String,

	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(nullable = false, unique = true)
	var balance: Int,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userId")
	val pointHistories: MutableList<PointHistory> = mutableListOf()
) : BaseEntity() {

	companion object {
		const val BALANCE_LIMIT = 1_000_000
	}

	fun updateUserUUID(uuid: String) {
		userUUID = uuid
	}

	fun charge(amount: Int): PointHistory {
		require(balance + amount <= BALANCE_LIMIT) { throw BadRequestException() }

		balance += amount

		val pointHistory = PointHistory.charge(id, amount)
		pointHistories.add(pointHistory)
		return pointHistory
	}

	fun use(amount: Int): PointHistory {
		require(balance >= amount) { throw BadRequestException() }

		balance -= amount

		val pointHistory = PointHistory.use(id, amount)
		pointHistories.add(pointHistory)
		return pointHistory
	}
}