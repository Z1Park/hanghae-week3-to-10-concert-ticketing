package kr.hhplus.be.server.domain.user

import io.mockk.every
import io.mockk.mockkObject
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.KSelect.Companion.field
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
	fun `uuid를 통해 유저 조회 시, 없는 uuid를 조회하면 CustomException이 발생한다`() {
		// given

		// when then
		assertThatThrownBy { sut.getByUuid("fdien129fvb470") }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `uuid 업데이트 시, 파라미터로 넘긴 uuid가 유저 정보에 반영되고 저장하는 메서드를 호출한다`() {
		// given
		val userId = 12L
		val user = Instancio.of(User::class.java)
			.set(field(User::id), userId)
			.set(field(User::userUUID), "beforeUUID")
			.create()
		val changeUUID = "newUUID"

		`when`(userRepository.findById(userId))
			.then { user }

		// when
		sut.saveUserUUID(userId, changeUUID)

		//then
		assertThat(user.userUUID).isNotEqualTo("beforeUUID")
		assertThat(user.userUUID).isEqualTo("newUUID")
		verify(userRepository).save(user)
	}

	@Test
	fun `uuid 업데이트 시, 없는 유저의 id를 받으면 CustomEntityNotFoundException이 발생한다`() {
		// given
		val noneUserId = 11L
		val userUUID = "myUserUUID"

		// when then
		assertThatThrownBy { sut.saveUserUUID(noneUserId, userUUID) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `충전 요청 시, 유저의 잔액을 증가시키고 충전 내역을 저장하는 메서드를 호출한다`() {
		// given
		val chargeAmount = 2000
		val userUUID = "myUserUUID"
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 1L)
			.set(field(User::balance), 8000)
			.set(field(User::pointHistories), mutableListOf<PointHistory>())
			.create()

		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::type), PointHistoryType.CHARGE)
			.set(field(PointHistory::amount), chargeAmount)
			.create()

		`when`(userRepository.findByUuidForUpdate(userUUID))
			.then { user }

		mockkObject(PointHistory.Companion)
		every { PointHistory.charge(user.id, chargeAmount) } returns pointHistory

		// when
		sut.charge(userUUID, chargeAmount)

		//then
		verify(userRepository).save(pointHistory)
		verify(userRepository).save(user)

		assertThat(user.balance).isEqualTo(10000)
	}

	@Test
	fun `충전 요청 시, 없는 userUUID로 요청하면 CustomEntityNotFoundException이 발생한다`() {
		// given
		val chargeAmount = 2000
		val userUUID = "myUserUUID"

		`when`(userRepository.findByUuidForUpdate(userUUID))
			.then { null }

		// when then
		assertThatThrownBy { sut.charge(userUUID, chargeAmount) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `사용 요청 시, 유저의 잔액을 차감하고 사용 내역을 저장하는 메서드를 호출한다`() {
		// given
		val useAmount = 2000
		val userUUID = "myUserUUID"
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 1L)
			.set(field(User::balance), 8000)
			.set(field(User::pointHistories), mutableListOf<PointHistory>())
			.create()

		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::type), PointHistoryType.USE)
			.set(field(PointHistory::amount), useAmount)
			.create()

		`when`(userRepository.findByUuidForUpdate(userUUID))
			.then { user }

		mockkObject(PointHistory.Companion)
		every { PointHistory.use(user.id, useAmount) } returns pointHistory

		// when
		sut.use(userUUID, useAmount)

		//then
		verify(userRepository).save(pointHistory)
		verify(userRepository).save(user)

		assertThat(user.balance).isEqualTo(6000)
	}

	@Test
	fun `사용 요청 시, 없는 userUUID로 요청하면 CustomException이 발생한다`() {
		// given
		val useAmount = 2000
		val userUUID = "myUserUUID"

		`when`(userRepository.findByUuidForUpdate(userUUID))
			.then { null }

		// when then
		assertThatThrownBy { sut.use(userUUID, useAmount) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `사용 요청 롤백 시, 유저와 포인트 히스토리를 조회하고 유저의 잔액을 사용만큼 증가시킨다`() {
		// given
		val userId = 31L
		val pointHistoryId = 48L
		val user = Instancio.of(User::class.java)
			.set(field(User::balance), 1300)
			.create()
		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::type), PointHistoryType.USE)
			.set(field(PointHistory::amount), 700)
			.create()

		`when`(userRepository.findById(userId))
			.then { user }
		`when`(userRepository.findPointHistoryById(pointHistoryId))
			.then { pointHistory }

		// when
		sut.rollbackUsePointHistory(userId, pointHistoryId)

		//then
		assertThat(user.balance).isEqualTo(2000)
		assertThat(user.pointHistories).doesNotContain(pointHistory)

		verify(userRepository).delete(pointHistory)
	}

	@Test
	fun `사용 요청 롤백 시, 사용 기록의 타입이 USE가 아니라면 CustomException이 발생한다`() {
		// given
		val userId = 33L
		val pointHistoryId = 93L
		val user = Instancio.of(User::class.java)
			.set(field(User::balance), 1300)
			.create()
		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::type), PointHistoryType.CHARGE)
			.set(field(PointHistory::amount), 700)
			.create()

		`when`(userRepository.findById(userId))
			.then { user }
		`when`(userRepository.findPointHistoryById(pointHistoryId))
			.then { pointHistory }

		// when then
		assertThatThrownBy { sut.rollbackUsePointHistory(userId, pointHistoryId) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ROLLBACK_FAIL)
	}
}