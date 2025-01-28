package kr.hhplus.be.server.domain.user

import io.mockk.every
import io.mockk.mockkObject
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.KSelect.Companion.field
import kr.hhplus.be.server.domain.user.model.PointHistory
import kr.hhplus.be.server.domain.user.model.PointHistoryType
import kr.hhplus.be.server.domain.user.model.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserUnitTest {

	@Test
	fun `충전 요청 시, 잔액을 충전 후 `() {
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
		user.charge(chargeAmount)

		//then
		assertThat(user.balance).isEqualTo(10000)
		assertThat(user.pointHistories).hasSize(1)
			.containsExactly(pointHistory)
	}

	@Test
	fun `충전 요청 시, 충전 후 잔액이 한도를 넘으면 CustomException이 발생한다`() {
		// given
		val chargeAmount = 10_001
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 1L)
			.set(field(User::balance), 990_000)
			.set(field(User::pointHistories), mutableListOf<PointHistory>())
			.create()

		// when then
		assertThatThrownBy { user.charge(chargeAmount) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.EXCEED_CHARGE_LIMIT)
	}

	@Test
	fun `포인트 사용 롤백 시, 사용내역에 따라 잔고가 다시 증가하고 사용 내역을 리스트에서 제거한다`() {
		// given
		val pointHistoryId = 13L
		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::id), pointHistoryId)
			.set(field(PointHistory::type), PointHistoryType.USE)
			.set(field(PointHistory::amount), 1500)
			.create()
		val user = Instancio.of(User::class.java)
			.set(field(User::id), 1L)
			.set(field(User::balance), 3000)
			.set(field(User::pointHistories), mutableListOf(pointHistory))
			.create()

		// when
		user.rollbackUse(pointHistoryId)

		//then
		assertThat(user.balance).isEqualTo(4500)
		assertThat(user.pointHistories).noneMatch { it.id == pointHistory.id }
	}

	@Test
	fun `포인트 사용 롤백 시, 충전 요청에 대해 롤백을 시도하면 CustomException이 발생한다`() {
		// given
		val pointHistoryId = 13L
		val pointHistory = Instancio.of(PointHistory::class.java)
			.set(field(PointHistory::id), pointHistoryId)
			.set(field(PointHistory::type), PointHistoryType.CHARGE)
			.create()
		val user = Instancio.of(User::class.java)
			.set(field(User::balance), 3000)
			.set(field(User::pointHistories), mutableListOf(pointHistory))
			.create()

		// when then
		assertThatThrownBy { user.rollbackUse(pointHistoryId) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ROLLBACK_FAIL)
	}
}