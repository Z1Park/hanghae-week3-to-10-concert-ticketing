package kr.hhplus.be.server.domain.queue

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity
import kr.hhplus.be.server.domain.queue.QueueActiveStatus.*
import java.time.LocalDateTime
import kotlin.math.max

@Entity
@Table(name = "queue")
class Queue(
	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(name = "token_uuid", nullable = false)
	var tokenUUID: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "activate_status", nullable = false)
	var activateStatus: QueueActiveStatus,

	@Column(name = "expired_at")
	var expiredAt: LocalDateTime? = null
) : BaseEntity() {

	companion object {
		fun createNewToken(userUUID: String, tokenUUID: String): Queue {
			return Queue(userUUID, tokenUUID, WAITING)
		}
	}

	fun isActivated(): Boolean {
		return activateStatus == ACTIVATED
	}

	fun activate(expiredAt: LocalDateTime) {
		if (activateStatus == WAITING) {
			activateStatus = ACTIVATED
			this.expiredAt = expiredAt
		}
	}

	fun deactivate() {
		if (activateStatus == ACTIVATED) {
			activateStatus = DEACTIVATED
		}
	}

	fun rollbackDeactivation() {
		if (activateStatus == DEACTIVATED) {
			activateStatus = ACTIVATED
		}
	}

	fun calculateWaitingOrder(lastActivatedQueue: Queue?): Long {
		val waitingOrder = this.id - (lastActivatedQueue?.id ?: 0) - 1
		return max(waitingOrder, 0L)
	}
}

enum class QueueActiveStatus {
	WAITING,
	ACTIVATED,
	DEACTIVATED
}