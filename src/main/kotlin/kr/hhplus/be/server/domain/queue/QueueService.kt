package kr.hhplus.be.server.domain.queue

import jakarta.transaction.Transactional
import kr.hhplus.be.server.common.ClockHolder
import kr.hhplus.be.server.domain.queue.QueueActiveStatus.ACTIVATED
import kr.hhplus.be.server.domain.queue.QueueActiveStatus.WAITING
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
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
		?: throw EntityNotFoundException.fromParam("Queue", "uuid", tokenUUID)

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

		waitingTokens.forEach() { it.activate(clockHolder.getCurrentTime()) }
		queueRepository.saveAll(waitingTokens)
	}
}