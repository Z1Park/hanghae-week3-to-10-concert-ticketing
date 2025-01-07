package kr.hhplus.be.server.domain.queue

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.BaseEntity
import java.time.LocalDateTime

@Entity
class Queue(
	@Column(name = "user_uuid", nullable = false)
	var userUUID: String,

	@Column(name = "token_uuid", nullable = false)
	var tokenUUID: String,

	@Enumerated(EnumType.STRING)
	@Column(name = "activate_status", nullable = false)
	var activateStatus: QueueActiveStatus,

	@Column(name = "expired_at", nullable = false)
	var expiredAt: LocalDateTime
) : BaseEntity() {

	companion object {
		fun createNewToken(userUUID: String, tokenUUID: String, clockHolder: ClockHolder): Queue {
			val expiredAt = clockHolder.getCurrentTime().plusMinutes(30)

			return Queue(userUUID, tokenUUID, QueueActiveStatus.WAITING, expiredAt)
		}
	}
}

enum class QueueActiveStatus {
	WAITING,
	ACTIVATED,
	DEACTIVATED
}