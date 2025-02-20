package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.common.outbox.OutboxEventStatus
import kr.hhplus.be.server.common.outbox.OutboxEventType
import kr.hhplus.be.server.infrastructure.payment.PaymentOutboxRepository
import kr.hhplus.be.server.infrastructure.payment.entity.PaymentOutboxMessage
import org.springframework.stereotype.Service

@Service
class PaymentOutboxService(
	private val paymentOutboxRepository: PaymentOutboxRepository
) {

	fun savePaymentInfo(payload: PaymentPayload) {
		val outboxMessage = PaymentOutboxMessage(
			payload.traceId,
			OutboxEventType.RESERVE,
			OutboxEventStatus.CREATED,
			payload.paymentId,
			payload.reservationId,
			payload.userId,
			payload.price
		)
		paymentOutboxRepository.save(outboxMessage)
	}

	fun processPaymentDataPlatformMessage(traceId: String) {
		val outboxMessage = paymentOutboxRepository.findByTraceId(traceId)
		require(outboxMessage != null) {
			throw CustomException(
				ErrorCode.ENTITY_NOT_FOUND,
				"아웃박스 데이터 저장이 제대로 이루어지지 않았습니다. traceId=${traceId}"
			)
		}

		outboxMessage.updateStatusProcessed()
		paymentOutboxRepository.save(outboxMessage)
	}
}