package kr.hhplus.be.server.domain.user.model

import kr.hhplus.be.server.domain.BaseDomain

class PointHistory(
	var type: PointHistoryType,

	var amount: Int,

	val userId: Long,

	id: Long = 0L
) : BaseDomain(id) {

	companion object {
		fun charge(userId: Long, amount: Int): PointHistory =
			PointHistory(PointHistoryType.CHARGE, amount, userId)

		fun use(userId: Long, amount: Int): PointHistory =
			PointHistory(PointHistoryType.USE, amount, userId)
	}
}