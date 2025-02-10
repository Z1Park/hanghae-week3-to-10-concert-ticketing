package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.domain.concert.ConcertService
import kr.hhplus.be.server.domain.payment.PaymentService
import kr.hhplus.be.server.domain.token.TokenService
import kr.hhplus.be.server.domain.user.UserService
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ReservationPaymentOrchestratorUnitTest {

	@InjectMocks
	private lateinit var sut: ReservationPaymentOrchestrator

	@Mock
	private lateinit var reservationService: ReservationService

	@Mock
	private lateinit var userService: UserService

	@Mock
	private lateinit var concertService: ConcertService

	@Mock
	private lateinit var paymentService: PaymentService

	@Mock
	private lateinit var tokenService: TokenService

	@Test
	fun `오케스트레이션 동작 테스트 - USE_POINT`() {
		// given
		val userId = 123L
		val testTime = LocalDateTime.of(2025, 1, 16, 11, 39, 40)
		sut.setupInitialRollbackInfo(userId, testTime)

		val pointHistoryId = 11L
		sut.successFlow(ReservationPaymentFlow.USE_POINT, pointHistoryId)

		// when
		sut.rollbackAll()

		//then
		verify(userService).rollbackUsePointHistory(userId, pointHistoryId)
	}

	@Test
	fun `오케스트레이션 동작 테스트 - CREATE_PAYMENT`() {
		// given
		val userId = 123L
		val testTime = LocalDateTime.of(2025, 1, 16, 11, 39, 40)
		sut.setupInitialRollbackInfo(userId, testTime)

		val paymentId = 123L
		sut.successFlow(ReservationPaymentFlow.CREATE_PAYMENT, paymentId)

		// when
		sut.rollbackAll()

		//then
		verify(paymentService).rollbackPayment(paymentId)
	}

	@Test
	fun `오케스트레이션 동작 테스트 - SOLD_OUT_RESERVATION`() {
		// given
		val userId = 123L
		val testTime = LocalDateTime.of(2025, 1, 16, 11, 39, 40)
		sut.setupInitialRollbackInfo(userId, testTime)

		val reservationId = 1L
		sut.successFlow(ReservationPaymentFlow.SOLD_OUT_RESERVATION, reservationId)

		// when
		sut.rollbackAll()

		//then
		verify(reservationService).rollbackReservation(reservationId, testTime)
	}

	@Test
	fun `오케스트레이션 동작 테스트 - SOLD_OUT_SEAT`() {
		// given
		val userId = 123L
		val testTime = LocalDateTime.of(2025, 1, 16, 11, 39, 40)
		sut.setupInitialRollbackInfo(userId, testTime)

		val seatId = 123L
		sut.successFlow(ReservationPaymentFlow.SOLD_OUT_SEAT, seatId)

		// when
		sut.rollbackAll()

		//then
		verify(concertService).rollbackSoldOutedConcertSeat(seatId, testTime)
	}

	@Test
	fun `clear 호출 후에 내부 필드가 null이 되어 NPE가 발생한다`() {
		// given
		val userId = 123L
		val testTime = LocalDateTime.of(2025, 1, 16, 11, 39, 40)
		sut.setupInitialRollbackInfo(userId, testTime)

		val expiredAt = LocalDateTime.of(2025, 1, 16, 11, 45, 10)
		sut.clear()

		// when then
		assertThatThrownBy { sut.successFlow(ReservationPaymentFlow.USE_POINT, 1L) }
			.isInstanceOf(NullPointerException::class.java)
	}
}