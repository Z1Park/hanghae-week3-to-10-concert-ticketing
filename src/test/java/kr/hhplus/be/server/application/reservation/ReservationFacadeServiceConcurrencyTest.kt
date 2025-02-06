package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import kr.hhplus.be.server.infrastructure.user.entity.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
@Import(ReservationFacadeConcurrencyTestConfig::class)
class ReservationFacadeServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ReservationFacadeService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val reservationJpaRepository: ReservationJpaRepository,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository,
	@Autowired private val paymentJpaRepository: PaymentJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	/**
	 * ThreadLocal로 인해 코루틴 사용 불가
	 */
	@Test
	fun `결제 요청 시, 동일한 예약에 대해 3번의 요청이 동시에 들어와도 결제는 1개만 생성되어야 한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)

		val userUUID = "myUserUUID"
		val user = UserEntity("김항해", userUUID, 60000)
		userJpaRepository.save(user)

		val concert = ConcertEntity("항해콘", "나가수")
		concertJpaRepository.save(concert)

		val schedule = ConcertScheduleEntity(50, testTime.plusHours(9), testTime.plusHours(13), concert.id)
		concertScheduleJpaRepository.save(schedule)

		val seat = ConcertSeatEntity(99, 15000, schedule.id, testTime.plusMinutes(5))
		concertSeatJpaRepository.save(seat)

		var reservation = ReservationEntity(testTime, 15000, user.id, concert.id, schedule.id, seat.id)
		reservationJpaRepository.save(reservation)

		val tokenUUID = "myTokenUUID"

		val cri1 = PaymentCri.Create(userUUID, tokenUUID, reservation.id)
		val cri2 = PaymentCri.Create(userUUID, tokenUUID, reservation.id)
		val cri3 = PaymentCri.Create(userUUID, tokenUUID, reservation.id)
		val cris = listOf(cri1, cri2, cri3)

		val repeatCount = 3
		val countDownLatch = CountDownLatch(repeatCount)
		val executors = Executors.newFixedThreadPool(repeatCount)

		// when
		for (i in 0 until repeatCount) {
			executors.execute {
				try {
					sut.payReservation(cris[i])
				} catch (e: Exception) {
				}
				countDownLatch.countDown()
			}
		}

		//then
		countDownLatch.await()

		val actualPayments = paymentJpaRepository.findAll()
		assertThat(actualPayments).hasSize(1)
	}
}

@TestConfiguration
class ReservationFacadeConcurrencyTestConfig {
	@Bean
	@Primary
	fun clockHolder(): ClockHolder = ClockHolder {
		LocalDateTime.of(2025, 1, 17, 12, 59, 59)
	}
}
