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
}