package kr.hhplus.be.server.infrastructure.payment.entity

import jakarta.persistence.*
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "payment_outbox_message")
class PaymentOutboxMessage(
	@Column(name = "trace_id", nullable = false)
	var traceId: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false)
	var eventType: OutboxEventType,

	@Enumerated(EnumType.STRING)
	@Column(name = "event_status", nullable = false)
	var eventStatus: OutboxEventStatus,

	@Column(name = "payment_id", nullable = false)
	var paymentId: Long,

	@Column(name = "reservation_id")
	var reservationId: Long?,

	@Column(name = "user_id")
	var userId: Long?,

	var price: Int?,

	id: Long = 0L
) : BaseEntity(id) {

	fun updateStatusProcessed() {
		if (eventStatus == OutboxEventStatus.CREATED) {
			eventStatus = OutboxEventStatus.PROCESSED
		}
	}
}