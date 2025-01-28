package kr.hhplus.be.server.domain.queue.model

import kr.hhplus.be.server.domain.BaseDomain
import java.time.LocalDateTime
import kotlin.math.max

class Queue(
	var userUUID: String,

	var tokenUUID: String,

	var activateStatus: QueueActiveStatus,

	var expiredAt: LocalDateTime? = null,

	id: Long = 0L
) : BaseDomain(id) {

	companion object {
		fun createNewToken(userUUID: String, tokenUUID: String): Queue {
			return Queue(userUUID, tokenUUID, QueueActiveStatus.WAITING)
		}
	}

	fun isActivated(): Boolean {
		return activateStatus == QueueActiveStatus.ACTIVATED
	}

	fun activate(expiredAt: LocalDateTime) {
		if (activateStatus == QueueActiveStatus.WAITING) {
			activateStatus = QueueActiveStatus.ACTIVATED
			this.expiredAt = expiredAt
		}
	}

	fun deactivate() {
		if (activateStatus == QueueActiveStatus.ACTIVATED) {
			activateStatus = QueueActiveStatus.DEACTIVATED
		}
	}

	fun rollbackDeactivation() {
		if (activateStatus == QueueActiveStatus.DEACTIVATED) {
			activateStatus = QueueActiveStatus.ACTIVATED
		}
	}

	fun calculateWaitingOrder(lastActivatedQueue: Queue?): Long {
		val waitingOrder = this.id - (lastActivatedQueue?.id ?: 0) - 1
		return max(waitingOrder, 0L)
	}
}