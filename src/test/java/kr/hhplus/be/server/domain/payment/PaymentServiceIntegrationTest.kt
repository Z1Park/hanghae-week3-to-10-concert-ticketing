package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PaymentServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: PaymentService,
	@Autowired private val paymentJpaRepository: PaymentJpaRepository
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `결제 요청 시, 새로운 Payment를 생성하여 저장한다`() {
		// given
		val command = PaymentCommand.Create(1000, 1L, 3L)

		// when
		val actual = sut.pay(command)

		//then
		val set = paymentJpaRepository.findByIdOrNull(actual.id)!!.toDomain()
		assertThat(set.price).isEqualTo(1000)
		assertThat(set.userId).isEqualTo(1L)
		assertThat(set.reservationId).isEqualTo(3L)
	}
}