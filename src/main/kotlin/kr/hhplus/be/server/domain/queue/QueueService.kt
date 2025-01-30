package kr.hhplus.be.server.domain.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.queue.QueueActiveStatus.ACTIVATED
import kr.hhplus.be.server.domain.queue.QueueActiveStatus.WAITING
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class QueueService(
	private val queueRepository: QueueRepository
) {

	companion object {
		const val ACTIVATE_COUNT_PER_SEC = 80
	}

	fun getByUuid(tokenUUID: String): Queue = queueRepository.findByUUID(tokenUUID)
		?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "tokenUUID=$tokenUUID")

	fun findLastActivatedQueue(): Queue? {
		val pageable = PageRequest.of(0, 1)
		val allActivatedQueue = queueRepository.findAllOrderByCreatedAtDesc(ACTIVATED, pageable)
		return allActivatedQueue.maxByOrNull { it.createdAt }
	}

	fun calculateWaitingInfo(queue: Queue, lastActivatedQueue: Queue?): QueueWaitingInfo {
		if (queue.isActivated()) {
			return QueueWaitingInfo(0, 0);
		}

		val waitingOrder = queue.calculateWaitingOrder(lastActivatedQueue)
		val expectedWaitingSeconds = ceil(waitingOrder.toDouble() / ACTIVATE_COUNT_PER_SEC).toInt()
		return QueueWaitingInfo(waitingOrder, expectedWaitingSeconds)
	}

	fun validateQueueToken(tokenUUID: String, requiredType: QueueActiveStatus) {
		val queue = queueRepository.findByUUID(tokenUUID)
			?: throw CustomException(ErrorCode.INVALID_QUEUE_TOKEN, "tokenUUID=$tokenUUID")

		if (requiredType == ACTIVATED) {
			require(queue.isActivated()) { throw CustomException(ErrorCode.REQUIRE_ACTIVATED_QUEUE_TOKEN) }
		}
	}

	@Transactional
	fun createNewToken(userUUID: String, tokenUUID: String): Queue {
		val newToken = Queue.createNewToken(userUUID, tokenUUID)
		return queueRepository.save(newToken)
	}

	fun expireTokens(clockHolder: ClockHolder) {
		val activatedTokens = queueRepository.findAllByActivateStatusAndExpiredAtBefore(ACTIVATED, clockHolder.getCurrentTime())

		activatedTokens.forEach { it.deactivate() }
		queueRepository.saveAll(activatedTokens)
	}

	fun activateTokens(clockHolder: ClockHolder) {
		val activateTokenCount = queueRepository.countByActivateStatusAndExpiredAtAfter(ACTIVATED, clockHolder.getCurrentTime())

		val pageable = PageRequest.of(0, ACTIVATE_COUNT_PER_SEC - activateTokenCount)
		val waitingTokens = queueRepository.findAllOrderByCreatedAt(WAITING, pageable)

		val expiredAt = clockHolder.getCurrentTime().plusMinutes(20)
		waitingTokens.forEach() { it.activate(expiredAt) }
		queueRepository.saveAll(waitingTokens)
	}

	@Transactional
	fun deactivateToken(tokenUUID: String): Queue {
		val token = queueRepository.findByUUID(tokenUUID)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "tokenUUID=$tokenUUID")

		token.deactivate()
		return queueRepository.save(token)
	}

	@Transactional
	fun rollbackDeactivateToken(tokenId: Long) {
		val token = queueRepository.findById(tokenId)
			?: throw CustomException(ErrorCode.ENTITY_NOT_FOUND, "tokenId=$tokenId")

		token.rollbackDeactivation()
		queueRepository.save(token)
	}
}