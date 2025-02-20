package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.PointUseSuccessEvent
import kr.hhplus.be.server.domain.user.UserPointOutboxService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserPointEventListener(
	private val userPointOutboxService: UserPointOutboxService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun savePointUseInfoToOutbox(event: PointUseSuccessEvent) {
		log.debug("유저 포인트 차감 정보 outbox 저장")

		userPointOutboxService.savePointUseInfo(event.toPointUsePayload())
	}
}