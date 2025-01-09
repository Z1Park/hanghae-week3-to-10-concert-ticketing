package kr.hhplus.be.server.domain.user

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "point_history")
class PointHistory(
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	var type: PointHistoryType,

	@Column(nullable = false)
	var amount: Int,

	@Column(name = "user_id", nullable = false)
	val userId: Long
) : BaseEntity() {

	companion object {
		fun charge(userId: Long, amount: Int): PointHistory =
			PointHistory(PointHistoryType.CHARGE, amount, userId)

		fun use(userId: Long, amount: Int): PointHistory =
			PointHistory(PointHistoryType.USE, amount, userId)
	}
}

enum class PointHistoryType {
	CHARGE,
	USE,
	;
}