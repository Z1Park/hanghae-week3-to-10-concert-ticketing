package kr.hhplus.be.server.infrastructure.concert.entity

import jakarta.persistence.*
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "concert_outbox_message")
class ConcertOutboxMessage(
	@Column(name = "trace_id", nullable = false)
	var traceId: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false)
	var eventType: OutboxEventType,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_status", nullable = false)
	var eventStatus: OutboxEventStatus,

	@Column(name = "concert_id")
	var concertId: Long?,

	@Column(name = "concert_schedule_id")
	var concertScheduleId: Long?,

	@Column(name = "concert_seat_id", nullable = false)
	var concertSeatId: Long,

	@Column(name = "expired_at")
	var expiredAt: LocalDateTime?,

	@Column(name = "origin_expired_at")
	var originExpiredAt: LocalDateTime?,

	id: Long = 0L
) : BaseEntity(id) {

	fun updateStatusProcessed() {
		if (eventStatus == OutboxEventStatus.CREATED) {
			eventStatus = OutboxEventStatus.PROCESSED
		}
	}

	fun updateStatusRollbacked() {
		if (eventStatus == OutboxEventStatus.PROCESSED) {
			eventStatus = OutboxEventStatus.ROLLBACKED
		}
	}
}