package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.ConcurrencyTestHelper
import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import kr.hhplus.be.server.domain.concert.ConcertSeat
import kr.hhplus.be.server.domain.user.User
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository
import kr.hhplus.be.server.infrastructure.queue.QueueJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
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

@SpringBootTest
@Import(TestConfig::class)
class ReservationFacadeServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ReservationFacadeService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val reservationJpaRepository: ReservationJpaRepository,
	@Autowired private val queueJpaRepository: QueueJpaRepository,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository,
	@Autowired private val paymentJpaRepository: PaymentJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `콘서트 에약 시, 동일한 좌석에 대한 5개의 요청 동시에 들어와도 예약은 1개만 생겨야한다`() {
		// given
		val user1 = User("김항해", "유저토큰1", 1000)
		val user2 = User("나항해", "유저토큰2", 2000)
		val user3 = User("박항해", "유저토큰3", 3000)
		val user4 = User("이항해", "유저토큰4", 4000)
		val user5 = User("송항해", "유저토큰5", 5000)
		val users = listOf(user1, user2, user3, user4, user5)
		userJpaRepository.saveAll(users)

		val concert = Concert("항해콘", "나가수")
		concertJpaRepository.save(concert)

		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)
		val schedule = ConcertSchedule(50, testTime.plusHours(10), testTime.plusHours(13), concert.id)
		concertScheduleJpaRepository.save(schedule)

		val seat = ConcertSeat(11, 900, schedule.id, testTime.minusMinutes(5))
		concertSeatJpaRepository.save(seat)

		val cri1 = ReservationCri.Create(user1.userUUID, concert.id, schedule.id, seat.id)
		val cri2 = ReservationCri.Create(user2.userUUID, concert.id, schedule.id, seat.id)
		val cri3 = ReservationCri.Create(user3.userUUID, concert.id, schedule.id, seat.id)
		val cri4 = ReservationCri.Create(user4.userUUID, concert.id, schedule.id, seat.id)
		val cri5 = ReservationCri.Create(user5.userUUID, concert.id, schedule.id, seat.id)
		val params = arrayOf(cri1, cri2, cri3, cri4, cri5)

		// when
		ConcurrencyTestHelper.runRepeatedly(sut::reserveConcertSeat, params, params.size)

		//then
		val actualReservations = reservationJpaRepository.findAll()
		assertThat(actualReservations).hasSize(1)
	}
}

@TestConfiguration
class TestConfig {
	@Bean
	@Primary
	fun clockHolder(): ClockHolder = TestClockHolder()
}

class TestClockHolder : ClockHolder {

	override fun getCurrentTime(): LocalDateTime {
		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)
		return testTime
	}

}