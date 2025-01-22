package kr.hhplus.be.server.application.user

import kotlinx.coroutines.*
import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.domain.user.PointHistoryType
import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class UserFacadeServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: UserFacadeService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val pointHistoryJpaRepository: PointHistoryJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `유저 충전 요청 시, 동일 유저가 1000원씩 3번 충전 요청을 동시에 보내면 3000원이 저장되어 있어야한다`() {
		// given
		val userUUID = "myUserUUID"
		val user = User("김항해", userUUID, 0)
		userJpaRepository.save(user)

		val context = newFixedThreadPoolContext(3, "유저 동시성 테스트")

		// when
		runBlocking {
			withContext(context) {
				coroutineScope {
					launch { sut.charge(userUUID, 1000) }
					launch { sut.charge(userUUID, 1000) }
					launch { sut.charge(userUUID, 1000) }
				}
			}
		}

		//then
		val actualUser = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(actualUser.balance).isEqualTo(3000)

		val actualPointHistories = pointHistoryJpaRepository.findAll().filter { it.userId == actualUser.id }
		assertThat(actualPointHistories).hasSize(3)
			.allMatch { it.type == PointHistoryType.CHARGE && it.amount == 1000 }
	}
}