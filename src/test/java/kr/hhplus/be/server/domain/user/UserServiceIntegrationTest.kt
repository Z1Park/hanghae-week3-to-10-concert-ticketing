package kr.hhplus.be.server.domain.user

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class UserServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: UserService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val pointHistoryJpaRepository: PointHistoryJpaRepository
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `유저 토큰 발급 시, 새로 발급된 uuid가 유저에 저장되고 그 값이 반환된다`() {
		// given
		val userUUID = "myUserUUID"
		val user = User("김항해", "before", 0)
		userJpaRepository.save(user)

		// when
		val actual = sut.saveUserUUID(user.id, userUUID)
		val set = userJpaRepository.findByIdOrNull(user.id)!!

		//then
		assertThat(actual.id).isEqualTo(user.id)

		assertThat(set.id).isEqualTo(user.id)
		assertThat(set.userUUID).isEqualTo("myUserUUID")
	}

	@Test
	fun `충전 요청 시, 유저를 조회 후 잔액을 증가시키고 충전 내역을 저장한다`() {
		// given
		val userUUID = "myUserUUID"
		val chargeAmount = 850

		val user = User("김항해", userUUID, 400)
		userJpaRepository.save(user)

		val pointHistory = PointHistory(PointHistoryType.CHARGE, 400, user.id)
		pointHistoryJpaRepository.save(pointHistory)

		// when
		val actual = sut.charge(userUUID, chargeAmount)

		//then
		assertThat(actual.balance).isEqualTo(1250)

		val set1 = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(set1.balance).isEqualTo(1250)

		val set2 = pointHistoryJpaRepository.findAll().filter { it.userId == user.id }
		assertThat(set2).hasSize(2)
			.anyMatch { it.type == PointHistoryType.CHARGE && it.amount == 400 }
			.anyMatch { it.type == PointHistoryType.CHARGE && it.amount == 850 }
	}

	@Test
	fun `사용 요청 시, 유저를 조회 후 잔액을 차감하고 사용 내역을 저장한다`() {
		// given
		val userUUID = "myUserUUID"
		val useAmount = 850

		val user = User("김항해", userUUID, 1400)
		userJpaRepository.save(user)

		val pointHistory = PointHistory(PointHistoryType.CHARGE, 1400, user.id)
		pointHistoryJpaRepository.save(pointHistory)

		// when
		val actual = sut.use(userUUID, useAmount)

		//then

		val set1 = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(set1.balance).isEqualTo(550)

		val set2 = pointHistoryJpaRepository.findAll().filter { it.userId == user.id }
		assertThat(set2).hasSize(2)
			.anyMatch { it.type == PointHistoryType.CHARGE && it.amount == 1400 }
			.anyMatch { it.type == PointHistoryType.USE && it.amount == 850 }

		val set3 = pointHistoryJpaRepository.findByIdOrNull(actual.id)!!
		assertThat(actual.id).isEqualTo(set3.id)
	}
}