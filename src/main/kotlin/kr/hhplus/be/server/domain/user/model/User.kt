package kr.hhplus.be.server.domain.user.model

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.BaseDomain

class User(
	var username: String,

	var userUUID: String,

	var balance: Int,

	val pointHistories: MutableList<PointHistory> = mutableListOf(),

	var version: Long = 0L,

	id: Long = 0L
) : BaseDomain(id) {

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

	fun rollbackUse(pointHistoryId: Long) {
		val pointHistory = pointHistories.first { it.id == pointHistoryId }
		require(pointHistory.type == PointHistoryType.USE) { throw CustomException(ErrorCode.ROLLBACK_FAIL) }

		balance += pointHistory.amount

		pointHistories.remove(pointHistory)
	}
}