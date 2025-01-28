package kr.hhplus.be.server.application.queue

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.domain.queue.model.QueueActiveStatus
import kr.hhplus.be.server.infrastructure.queue.QueueJpaRepository
import kr.hhplus.be.server.infrastructure.queue.entity.QueueJpaEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class QueueFacadeServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: QueueFacadeService,
	@Autowired private val uuidGenerator: UuidGenerator,
	@Autowired private val queueJpaRepository: QueueJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `대기 정보 조회 시, 가장 최근의 활성 토큰과 유저 토큰 사이에 80명이 있다면 대기번호 80을 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 10, 49, 56)
		for (i in 1L..10L) {
			val activatedQueue = QueueJpaEntity("activatedUserUUID$i", "activatedTokenUUID$i", QueueActiveStatus.ACTIVATED)
			queueJpaRepository.save(activatedQueue)
		}

		for (i in 11L..90L) {
			val waitingQueue = QueueJpaEntity("waitingUserUUID$i", "waitingTokenUUID$i", QueueActiveStatus.WAITING)
			queueJpaRepository.save(waitingQueue)
		}

		val queue = QueueJpaEntity("myUserUUID", "myTokenUUID", QueueActiveStatus.WAITING, testTime.plusMinutes(2))
		queueJpaRepository.save(queue)

		// when
		val actual = sut.getWaitingInfo(queue.tokenUUID)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(80)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(1)
	}

	@Test
	fun `대기열 토큰 발급 요청 시, 새로 토큰을 발급 후 반환된 uuid가 토큰에 저장되어 있다`() {
		// given
		val userUUID = "myUserUUID"

		// when
		val set = sut.issueQueueToken(userUUID, uuidGenerator)
		val actual = queueJpaRepository.findAll().first { it.tokenUUID == set }!!

		//then
		assertThat(actual.userUUID).isEqualTo("myUserUUID")
		assertThat(actual.tokenUUID).isEqualTo(set)
	}
}