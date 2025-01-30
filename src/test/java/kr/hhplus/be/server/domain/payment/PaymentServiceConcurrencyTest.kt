package kr.hhplus.be.server.domain.payment

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class PaymentServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: PaymentService,
	@Autowired private val paymentJpaRepository: PaymentJpaRepository
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `결제 요청 시, 동일한 예약에 대해 3번의 결제 요청이 동시에 들어오면 1개의 결제만 생성된다`() {
		// given
		val userId = 13L
		val reservationId = 99L
		val cri = PaymentCommand.Create(1000, userId, reservationId)

		val repeat = 3
		val countDownLatch = CountDownLatch(repeat)
		val executors = Executors.newFixedThreadPool(repeat)

		// when
		var successCount = 0
		val exceptions = mutableListOf<Exception>()
		for (i in 0 until repeat) {
			executors.execute {
				try {
					sut.pay(cri)
					successCount++
				} catch (e: Exception) {
					exceptions.add(e)
				}
				countDownLatch.countDown()
			}
		}

		//then
		countDownLatch.await()

		assertThat(successCount).isEqualTo(1)
		assertThat(exceptions).hasSize(2)
			.allMatch { it is DataIntegrityViolationException }

		val actual = paymentJpaRepository.findAll()
		assertThat(actual).hasSize(1)
	}
}