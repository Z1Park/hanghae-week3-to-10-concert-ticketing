package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.UuidGenerator
import kr.hhplus.be.server.domain.user.User
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
	@Autowired private val userJpaRepository: UserJpaRepository
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
}