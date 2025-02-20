package kr.hhplus.be.server.application.event.listener

import kr.hhplus.be.server.application.event.ConcertMakeSoldOutEvent
import kr.hhplus.be.server.application.event.ConcertPreoccupySuccessEvent
import kr.hhplus.be.server.domain.concert.ConcertOutboxService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ConcertEventListener(
	private val concertOutboxService: ConcertOutboxService
) {
	private val log = LoggerFactory.getLogger(javaClass)

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun saveConcertSeatPreoccupyInfoToOutbox(event: ConcertPreoccupySuccessEvent) {
		log.debug("콘서트 좌석 선점 정보 outbox 저장")

		concertOutboxService.saveConcertPreoccupyInfo(event.toConcertPreoccupyPayload())
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	fun saveConcertSeatSoldOutInfoToOutbox(event: ConcertMakeSoldOutEvent) {
		log.debug("콘서트 좌석 만료 정보 outbox 저장")

		concertOutboxService.saveConcertSeatSoldOutInfo(event.toConcertSeatSoldOutPayload())
	}
}