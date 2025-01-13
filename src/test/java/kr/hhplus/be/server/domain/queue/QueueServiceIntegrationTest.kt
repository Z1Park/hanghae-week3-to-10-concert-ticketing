package kr.hhplus.be.server.domain.queue

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.queue.QueueJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@SpringBootTest
class QueueServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: QueueService,
	@Autowired private val queueJpaRepository: QueueJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `토큰 만료 요청 시, 10개의 활성 토큰 중 만료 시간이 지난 5개의 토큰들만 만료 상태로 변경된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 23, 56, 46)
		for (i in 1L..10L) {
			val expiredAt = if (i % 2 == 0L) testTime.plusNanos(1000) else testTime.minusSeconds(1000)
			val activatedQueue = Queue("activatedUserUUID$i", "activatedTokenUUID$i", QueueActiveStatus.ACTIVATED, expiredAt)
			queueJpaRepository.save(activatedQueue)
		}

		// when
		sut.expireTokens() { testTime }

		//then
		val allQueues = queueJpaRepository.findAll()
		val activateCount = allQueues.count { it.activateStatus == QueueActiveStatus.ACTIVATED }
		val deactivateCount = allQueues.count { it.activateStatus == QueueActiveStatus.DEACTIVATED }

		assertThat(activateCount).isEqualTo(5)
		assertThat(deactivateCount).isEqualTo(5)
	}

	@Test
	fun `토큰 진입 요청 시, 10명 활성화 90명 대기하고 있다면 그 중 가장 오래 기다린 70명의 토큰이 활성 상태로 변경된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 10, 49, 56)
		for (i in 1L..10L) {
			val activatedQueue = Queue("activatedUserUUID$i", "activatedTokenUUID$i", QueueActiveStatus.ACTIVATED, testTime.plusMinutes(10))
			queueJpaRepository.save(activatedQueue)
		}
		for (i in 11L..100L) {
			val waitingQueue = Queue("waitingUserUUID$i", "waitingTokenUUID$i", QueueActiveStatus.WAITING)
			queueJpaRepository.save(waitingQueue)
		}

		// when
		sut.activateTokens() { testTime }

		//then
		val allQueues = queueJpaRepository.findAll()
		val activatedQueues = allQueues.filter { it.isActivated() }
		val minWaitingTokenTime = allQueues.filter { it.activateStatus == QueueActiveStatus.WAITING }.minOfOrNull { it.createdAt }!!
		val waitingCount = allQueues.count { it.activateStatus == QueueActiveStatus.WAITING }

		assertThat(waitingCount).isEqualTo(20)
		assertThat(activatedQueues).hasSize(80)
			.allMatch { it.expiredAt != null }
			.allMatch { it.createdAt.isBefore(minWaitingTokenTime) }
	}

	@Test
	fun `토큰 비활성화 시, tokenUUID로 조회한 토큰을 비활성화 시킨다`() {
		// given
		val tokenUUID = "myTokenUUID"
		val token = Queue("myUserUUID", tokenUUID, QueueActiveStatus.ACTIVATED)
		queueJpaRepository.save(token)

		// when
		sut.deactivateToken(tokenUUID)

		//then
		val actual = queueJpaRepository.findByIdOrNull(token.id)!!
		assertThat(actual.activateStatus).isEqualTo(QueueActiveStatus.DEACTIVATED)
	}
}