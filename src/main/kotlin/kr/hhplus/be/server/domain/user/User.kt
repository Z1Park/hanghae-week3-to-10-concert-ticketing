package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
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
		require(balance + amount <= BALANCE_LIMIT) {
			throw CustomException(ErrorCode.EXCEED_CHARGE_LIMIT, "balance=$balance, amount=$amount")
		}

		balance += amount

		val pointHistory = PointHistory.charge(id, amount)
		pointHistories.add(pointHistory)
		return pointHistory
	}

	fun use(amount: Int): PointHistory {
		require(balance >= amount) {
			throw CustomException(ErrorCode.NOT_ENOUGH_POINT, "balance=$balance, amount=$amount")
		}

		balance -= amount

		val pointHistory = PointHistory.use(id, amount)
		pointHistories.add(pointHistory)
		return pointHistory
	}

	fun rollbackUse(pointHistory: PointHistory) {
		require(pointHistory.type == PointHistoryType.USE) { throw CustomException(ErrorCode.ROLLBACK_FAIL) }

		balance += pointHistory.amount

		pointHistories.remove(pointHistory)
	}
}