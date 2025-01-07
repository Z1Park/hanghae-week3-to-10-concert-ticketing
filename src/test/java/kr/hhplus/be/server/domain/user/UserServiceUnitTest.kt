package kr.hhplus.be.server.domain.user

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

@ExtendWith(MockitoExtension::class)
class UserServiceUnitTest {

	@InjectMocks
	private lateinit var sut: UserService

	@Mock
	private lateinit var userRepository: UserRepository

	@Test
	fun `id를 통해 유저 조회 시, userRepository의 getById 메서드를 호출한다`() {
		// given
		val userId = 1L
		val user = Instancio.of(User::class.java)
			.set(field(User::id), userId)
			.set(field(User::username), "김항해")
			.create()

		`when`(userRepository.findById(userId)).then { user }

		// when
		val actual = sut.getById(userId)

		//then
		verify(userRepository).findById(userId)

		assertThat(actual.id).isEqualTo(1L)
		assertThat(actual.username).isEqualTo("김항해")
	}

	@Test
	fun `유저 조회 시, 없는 유저를 조회하면 EntityNotFoundException이 발생한다`() {
		// given
		val noneUserId = 11L

		// when then
		assertThatThrownBy { sut.getById(noneUserId) }
			.isInstanceOf(EntityNotFoundException::class.java)
			.hasMessage("User 엔티티를 찾을 수 없습니다. Id=11")
	}

	@Test
	fun `uuid를 통해 유저 조회 시, userRepository의 getByUuid 메서드를 호출한다`() {
		// given
		val uuid = "asidf8923n212"
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 2L)
			.set(field(User::username), "김항해")
			.set(field(User::userUUID), uuid)
			.create()

		`when`(userRepository.findByUuid(uuid)).then { user }

		// when
		val actual = sut.getByUuid(uuid)

		//then
		verify(userRepository).findByUuid(uuid)

		assertThat(actual.id).isEqualTo(2L)
		assertThat(actual.username).isEqualTo("김항해")
		assertThat(actual.userUUID).isEqualTo("asidf8923n212")
	}

	@Test
	fun `uuid를 통해 유저 조회 시, 없는 uuid를 조회하면 EntityNotFoundException이 발생한다`() {
		// given

		// when then
		assertThatThrownBy { sut.getByUuid("fdien129fvb470") }
			.isInstanceOf(EntityNotFoundException::class.java)
			.hasMessage("User 엔티티를 찾을 수 없습니다. uuid=fdien129fvb470")
	}

	@Test
	fun `uuid 업데이트 시, 파라미터로 넘긴 uuid가 User에 저장되고 save 메서드를 호출한다`() {
		// given
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 12L)
			.set(field(User::userUUID), "beforeUUID")
			.create()
		val changeUUID = "newUUID"

		// when
		sut.updateUserUuid(user, changeUUID)

		//then
		assertThat(user.userUUID).isNotEqualTo("beforeUUID")
		assertThat(user.userUUID).isEqualTo("newUUID")
		verify(userRepository).save(user)
	}
}