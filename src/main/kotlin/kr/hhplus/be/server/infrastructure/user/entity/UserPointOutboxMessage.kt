package kr.hhplus.be.server.infrastructure.user.entity

import jakarta.persistence.*
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "user_point_outbox_message")
class UserPointOutboxMessage(
	@Column(name = "trace_id", nullable = false)
	var traceId: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false)
	var eventType: OutboxEventType,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_status", nullable = false)
	var eventStatus: OutboxEventStatus,

	@Column(name = "user_id", nullable = false)
	var userId: Long,

	@Column(name = "origin_balance")
	var originBalance: Int?,

	@Column(name = "point_history_id")
	var pointHistoryId: Long?,

	id: Long = 0L
) : BaseEntity(id) {

	fun updateStatusRollbacked() {
		if (eventStatus == OutboxEventStatus.PROCESSED) {
			eventStatus = OutboxEventStatus.ROLLBACKED
		}
	}
}