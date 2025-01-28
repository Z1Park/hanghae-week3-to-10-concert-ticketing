package kr.hhplus.be.server.infrastructure.queue.entity

import jakarta.persistence.*
import kr.hhplus.be.server.domain.queue.model.Queue
import kr.hhplus.be.server.domain.queue.model.QueueActiveStatus
import kr.hhplus.be.server.infrastructure.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "queue")
class QueueJpaEntity(
	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(name = "token_uuid", nullable = false)
	var tokenUUID: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "activate_status", nullable = false)
	var activateStatus: QueueActiveStatus,

	@Column(name = "expired_at")
	var expiredAt: LocalDateTime? = null,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(queue: Queue) : this(
		id = queue.id,
		userUUID = queue.userUUID,
		tokenUUID = queue.tokenUUID,
		activateStatus = queue.activateStatus,
		expiredAt = queue.expiredAt
	)

	fun toDomain(): Queue = Queue(
		id = id,
		userUUID = userUUID,
		tokenUUID = tokenUUID,
		activateStatus = activateStatus,
		expiredAt = expiredAt
	)
}