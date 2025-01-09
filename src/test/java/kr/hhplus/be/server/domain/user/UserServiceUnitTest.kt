package kr.hhplus.be.server.domain.user

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
	fun `uuid 업데이트 시, 파라미터로 넘긴 uuid가 유저 정보에 반영되고 저장하는 메서드를 호출한다`() {
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

	@Test
	fun `충전 요청 시, 유저의 잔액을 증가시키고 충전 내역을 저장하는 메서드를 호출한다`() {
		// given
		val chargeAmount = 2000
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 1L)
			.set(field(User::balance), 8000)
			.set(field(User::pointHistories), mutableListOf<PointHistory>())
			.create()

		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::type), PointHistoryType.CHARGE)
			.set(field(PointHistory::amount), chargeAmount)
			.create()

		mockkObject(PointHistory.Companion)
		every { PointHistory.charge(user.id, chargeAmount) } returns pointHistory

		// when
		sut.charge(user, chargeAmount)

		//then
		verify(userRepository).save(pointHistory)
		verify(userRepository).save(user)

		assertThat(user.balance).isEqualTo(10000)
	}
}