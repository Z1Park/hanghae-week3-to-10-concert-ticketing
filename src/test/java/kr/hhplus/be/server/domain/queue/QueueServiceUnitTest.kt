package kr.hhplus.be.server.domain.queue

import io.mockk.every
import io.mockk.mockkObject
import kr.hhplus.be.server.domain.KSelect.Companion.field
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class QueueServiceUnitTest {

	@InjectMocks
	private lateinit var sut: QueueService

	@Mock
	private lateinit var queueRepository: QueueRepository

	@Test
	fun `tokenUUID를 통해 토큰 조회 시, 없는 토큰이라면 EntityNotFoundException이 발생한다`() {
		// given
		val tokenUUID = "myTokenUUID"

		`when`(queueRepository.findByUUID(tokenUUID)).then { null }

		// when then
		assertThatThrownBy { sut.getByUuid(tokenUUID) }
			.isInstanceOf(EntityNotFoundException::class.java)
			.hasMessage("Queue 엔티티를 찾을 수 없습니다. uuid=myTokenUUID")
	}

	@Test
	fun `가장 최근 활성화된 토큰 조회 시, 활성화된 토큰이 없으면 null을 반환한다`() {
		// given
		val pageable = PageRequest.of(0, 1)
		`when`(queueRepository.findAllOrderByCreatedAtDesc(QueueActiveStatus.ACTIVATED, pageable))
			.then { emptyList<Queue>() }

		// when
		val lastActivatedQueue = sut.findLastActivatedQueue()

		//then
		assertThat(lastActivatedQueue).isNull()
	}

	@Test
	fun `가장 최근 활성화된 토큰 조회 시, 페이지네이션이 적용되지 않더라도 가장 최근의 토큰값만 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 10, 13, 30)
		val pageable = PageRequest.of(0, 1)
		val queue1 = Instancio.of(Queue::class.java)
			.set(field(Queue::createdAt), testTime)
			.create()
		val queue2 = Instancio.of(Queue::class.java)
			.set(field(Queue::createdAt), testTime.plusNanos(1))
			.create()
		val queue3 = Instancio.of(Queue::class.java)
			.set(field(Queue::createdAt), testTime.minusNanos(1))
			.create()

		`when`(queueRepository.findAllOrderByCreatedAtDesc(QueueActiveStatus.ACTIVATED, pageable))
			.then { listOf(queue1, queue2, queue3) }

		// when
		val lastActivatedQueue = sut.findLastActivatedQueue()

		//then
		assertThat(lastActivatedQueue).isEqualTo(queue2)
	}

	@Test
	fun `대기번호 조회 시, 가장 최근에 활성화된 토큰과의 차이를 통해 대기번호와 예상 대기시간을 계산 후 반환한다`() {
		// given
		val myQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 2482L)
			.set(field(Queue::activateStatus), QueueActiveStatus.WAITING)
			.create()
		val lastActivatedQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 1845L)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()

		// when
		val actual = sut.calculateWaitingInfo(myQueue, lastActivatedQueue)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(636)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(8)
	}

	@Test
	fun `대기번호 조회 시, 최근 활성 토큰의 id가 현재 id보다 더 크다면 0의 대기번호와 0의 예상 대기시간을 반환한다`() {
		// given
		val myQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 2482L)
			.set(field(Queue::activateStatus), QueueActiveStatus.WAITING)
			.create()
		val lastActivatedQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 2483L)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()

		// when
		val actual = sut.calculateWaitingInfo(myQueue, lastActivatedQueue)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(0)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(0)
	}

	@Test
	fun `대기번호 조회 시, 이미 활성화된 상태라면 0의 대기번호와 0의 예상 대기시간을 반환한다`() {
		// given
		val myQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 2482L)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val lastActivatedQueue = Instancio.of(Queue::class.java)
			.set(field(Queue::id), 1L)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()

		// when
		val actual = sut.calculateWaitingInfo(myQueue, lastActivatedQueue)

		//then
		assertThat(actual.myWaitingOrder).isEqualTo(0)
		assertThat(actual.expectedWaitingSeconds).isEqualTo(0)
	}

	@Test
	fun `토큰 저장 시, 전달받은 토큰을 저장하는 메서드를 호출한다`() {
		// given
		val userUUID = "myUserUUID"
		val tokenUUID = "myTokenUUID"
		val queue = Instancio.of(Queue::class.java)
			.set(field(Queue::userUUID), userUUID)
			.set(field(Queue::tokenUUID), tokenUUID)
			.create()

		mockkObject(Queue.Companion)
		every { Queue.createNewToken(userUUID, tokenUUID) } returns queue

		// when
		sut.createNewToken(userUUID, tokenUUID)

		//then
		verify(queueRepository).save(queue)
	}

	@Test
	fun `토큰 만료 요청 시, 활성화된 토큰을 조회 후 만료 시키고 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 11, 33, 45)

		val token1 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val token2 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val token3 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val tokens = listOf(token1, token2, token3)

		`when`(queueRepository.findAllByActivateStatusAndExpiredAtBefore(QueueActiveStatus.ACTIVATED, testTime))
			.then { tokens }

		// when
		sut.expireTokens() { testTime }

		//then
		assertThat(tokens).allMatch { it.activateStatus == QueueActiveStatus.DEACTIVATED }

		verify(queueRepository).findAllByActivateStatusAndExpiredAtBefore(QueueActiveStatus.ACTIVATED, testTime)
		verify(queueRepository).saveAll(tokens)
	}

	@Test
	fun `토큰 활성화 요청 시, 활성화 토큰의 수를 조회 후 빈자리만큼 대기 중인 토큰을 활성화시킨다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 11, 33, 45)

		`when`(queueRepository.countByActivateStatusAndExpiredAtAfter(QueueActiveStatus.ACTIVATED, testTime))
			.then { 77 }

		val pageable = PageRequest.of(0, 3)
		val token1 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val token2 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val token3 = Instancio.of(Queue::class.java)
			.set(field(Queue::activateStatus), QueueActiveStatus.ACTIVATED)
			.create()
		val tokens = listOf(token1, token2, token3)
		`when`(queueRepository.findAllOrderByCreatedAt(QueueActiveStatus.WAITING, pageable))
			.then { tokens }

		// when
		sut.activateTokens() { testTime }

		//then
		assertThat(tokens).hasSize(3)
			.allMatch { it.activateStatus == QueueActiveStatus.ACTIVATED }
	}
}