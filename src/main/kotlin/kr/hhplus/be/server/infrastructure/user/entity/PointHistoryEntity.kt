package kr.hhplus.be.server.infrastructure.user.entity

import jakarta.persistence.*
import kr.hhplus.be.server.domain.user.model.PointHistory
import kr.hhplus.be.server.domain.user.model.PointHistoryType
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "point_history")
class PointHistoryEntity(
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	var type: PointHistoryType,

	@Column(nullable = false)
	var amount: Int,

	@Column(name = "user_id", nullable = false)
	val userId: Long,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(pointHistory: PointHistory) : this(
		id = pointHistory.id,
		type = pointHistory.type,
		amount = pointHistory.amount,
		userId = pointHistory.userId
	)

	fun toDomain(): PointHistory = PointHistory(
		id = id,
		type = type,
		amount = amount,
		userId = userId
	)
}
