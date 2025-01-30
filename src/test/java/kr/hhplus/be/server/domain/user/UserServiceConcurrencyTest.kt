package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class UserServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: UserService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val pointHistoryJpaRepository: PointHistoryJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `유저 충전 요청 시, 동일 유저가 1000원씩 3번 충전 요청을 동시에 보내면 1번만 성공하고 2번은 실패하여 1000원만 충전된다`() {
		// given
		val userUUID = "myUserUUID"
		val user = User("김항해", userUUID, 0)
		userJpaRepository.save(user)

		val repeat = 3
		val countDownLatch = CountDownLatch(repeat)
		val executors = Executors.newFixedThreadPool(repeat)

		// when
		var successCount = 0
		val exceptions = mutableListOf<Exception>()
		for (i in 0 until repeat) {
			executors.execute {
				try {
					sut.charge(userUUID, 1000)
					successCount++
				} catch (e: Exception) {
					exceptions.add(e)
				}
				countDownLatch.countDown()
			}
		}

		//then
		countDownLatch.await()

		assertThat(successCount).isEqualTo(1)
		assertThat(exceptions).hasSize(2)
			.allMatch { it is OptimisticLockingFailureException }

		val actualUser = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(actualUser.balance).isEqualTo(1000)

		val actualPointHistories = pointHistoryJpaRepository.findAll().filter { it.userId == actualUser.id }
		assertThat(actualPointHistories).hasSize(1)
			.allMatch { it.type == PointHistoryType.CHARGE && it.amount == 1000 }
	}

	@Test
	fun `유저 사용 요청 시, 동일 유저가 500원씩 3번 사용 요청을 동시에 보내면 1번만 성공하고 2번은 실패하여 500원만 사용된다`() {
		// given
		val userUUID = "myUserUUID"
		val user = User("김항해", userUUID, 2000)
		userJpaRepository.save(user)

		val repeat = 3
		val countDownLatch = CountDownLatch(repeat)
		val executors = Executors.newFixedThreadPool(repeat)

		// when
		var successCount = 0
		val exceptions = mutableListOf<Exception>()
		for (i in 0 until repeat) {
			executors.execute {
				try {
					sut.use(userUUID, 500)
					successCount++
				} catch (e: Exception) {
					exceptions.add(e)
				}
				countDownLatch.countDown()
			}
		}

		//then
		countDownLatch.await()

		assertThat(successCount).isEqualTo(1)
		assertThat(exceptions).hasSize(2)
			.allMatch { it is OptimisticLockingFailureException }

		val actualUser = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(actualUser.balance).isEqualTo(1500)

		val actualPointHistories = pointHistoryJpaRepository.findAll().filter { it.userId == actualUser.id }
		assertThat(actualPointHistories).hasSize(1)
			.allMatch { it.type == PointHistoryType.USE && it.amount == 500 }
	}
}