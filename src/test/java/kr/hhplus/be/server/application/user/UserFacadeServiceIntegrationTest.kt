package kr.hhplus.be.server.application.user

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.component.UuidGenerator
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import kr.hhplus.be.server.infrastructure.user.entity.UserEntity
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
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `유저 토큰 발급 시, 새로 발급된 uuid가 유저에 저장되고 그 값이 반환된다`() {
		// given
		val user = UserEntity("김항해", "before", 0)
		userJpaRepository.save(user)

		// when
		val issuedUserToken = sut.issueUserToken(user.id, uuidGenerator)
		val actual = userJpaRepository.findByIdOrNull(user.id)!!

		//then
		assertThat(actual.id).isEqualTo(user.id)
		assertThat(actual.userUUID).isNotEqualTo("before")
		assertThat(actual.userUUID).isEqualTo(issuedUserToken)
	}
}