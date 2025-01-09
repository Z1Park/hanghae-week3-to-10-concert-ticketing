package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.domain.user.PointHistory
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
class UserFacadeServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: UserFacadeService,
	@Autowired private val uuidGenerator: UuidGenerator,
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
		val user = User("김항해", "before", 0)
		userJpaRepository.save(user)

		// when
		val issuedUserToken = sut.issueUserToken(user.id, uuidGenerator)
		val actual = userJpaRepository.findByIdOrNull(user.id)!!

		//then
		assertThat(actual.id).isEqualTo(user.id)
		assertThat(actual.userUUID).isNotEqualTo("before")
		assertThat(actual.userUUID).isEqualTo(issuedUserToken)
	}

	@Test
	fun `유저 잔액 조회 시, 유저를 조회 후 저장되어있는 유저의 잔액을 반환한다`() {
		// given
		val userUUID = "myUserUUID"
		val user = User("김항해", userUUID, 8000)
		userJpaRepository.save(user)

		// when
		val actual = sut.getUserBalance(userUUID)

		//then
		assertThat(actual).isEqualTo(8000)
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
		assertThat(actual).isEqualTo(1250)

		val set1 = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(set1.balance).isEqualTo(1250)

		val set2 = pointHistoryJpaRepository.findAll().filter { it.userId == user.id }
		assertThat(set2).hasSize(2)
			.anyMatch { it.type == PointHistoryType.CHARGE && it.amount == 400 }
			.anyMatch { it.type == PointHistoryType.CHARGE && it.amount == 850 }
	}
}