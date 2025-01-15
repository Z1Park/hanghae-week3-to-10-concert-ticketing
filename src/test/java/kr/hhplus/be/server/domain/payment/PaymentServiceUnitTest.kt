package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.KSelect.Companion.field
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
class PaymentServiceUnitTest {

	@InjectMocks
	private lateinit var sut: PaymentService

	@Mock
	private lateinit var paymentRepository: PaymentRepository

	@Test
	fun `결제 요청 시, 결제를 생성하고 저장하는 메서드를 호출한다`() {
		// given
		val price = 15000
		val userId = 1L
		val reservationId = 428L
		val command = PaymentCommand.Create(price, userId, reservationId)
		val payment = command.toPayment()

		`when`(paymentRepository.findByUserIdAndReservationId(userId, reservationId))
			.then { null }

		// when
		sut.pay(command)

		//then
		verify(paymentRepository).save(payment)
	}

	@Test
	fun `결제 요청 시, 이미 동일 userId와 reservationId로 결제한 적이 있다면 CustomException이 발생한다`() {
		// given
		val price = 15000
		val userId = 1L
		val reservationId = 428L
		val command = PaymentCommand.Create(price, userId, reservationId)
		val payment = Instancio.of(Payment::class.java)
			.set(field(Payment::price), price)
			.set(field(Payment::userId), userId)
			.set(field(Payment::reservationId), reservationId)
			.create()

		`when`(paymentRepository.findByUserIdAndReservationId(userId, reservationId))
			.then { payment }

		// when then
		assertThatThrownBy { sut.pay(command) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_PAYED_RESERVATION)
	}

	@Test
	fun `롤백 요청 시, 결제를 삭제하는 메서드를 호출한다`() {
		// given
		val payment = Instancio.of(Payment::class.java).create()

		// when
		sut.rollbackPayment(payment)

		//then
		verify(paymentRepository).delete(payment)
	}
}