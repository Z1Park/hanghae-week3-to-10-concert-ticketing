package kr.hhplus.be.server.application.reservation

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.user.model.PointHistoryType
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import kr.hhplus.be.server.infrastructure.payment.PaymentJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
import kr.hhplus.be.server.infrastructure.user.PointHistoryJpaRepository
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
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@SpringBootTest
@Import(ReservationFacadeIntegrationTestConfig::class)
class ReservationFacadeServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ReservationFacadeService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val pointHistoryJpaRepository: PointHistoryJpaRepository,
	@Autowired private val reservationJpaRepository: ReservationJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository,
	@Autowired private val paymentJpaRepository: PaymentJpaRepository,
) {

	@Autowired
	private lateinit var concertScheduleJpaRepository: ConcertScheduleJpaRepository

	@Autowired
	private lateinit var concertJpaRepository: ConcertJpaRepository

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `예약 요청 시, 좌석 선점, 예약 생성 로직이 수행된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)

		val userUUID = "myUserUUID"
		val user = UserEntity("김항해", userUUID, 50000)
		userJpaRepository.save(user)

		val concert = ConcertEntity("항해콘", "나가수")
		concertJpaRepository.save(concert)

		val schedule = ConcertScheduleEntity(50, testTime, testTime.plusHours(5), concert.id)
		concertScheduleJpaRepository.save(schedule)

		val seat = ConcertSeatEntity(1, 15000, schedule.id, testTime.minusSeconds(1))
		concertSeatJpaRepository.save(seat)

		val create = ReservationCri.Create(userUUID, concert.id, schedule.id, seat.id)

		// when
		sut.reserveConcertSeat(create)

		//then
		val actualSeat = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actualSeat.reservedUntil).isEqualTo(testTime.plusMinutes(5))

		val actualReservations = reservationJpaRepository.findAll()
		assertThat(actualReservations).hasSize(1)
			.allMatch { it.userId == user.id && it.concertSeatId == seat.id && it.expiredAt == testTime.plusMinutes(5) }
	}

	@Test
	fun `결제 요청 시, 유저 포인트 차감, 결제 생성, 예약-좌석 매진, 토큰 비활성화 로직이 수행된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59) // config의 현재시간
		val expiredAt = testTime.plusNanos(1000)

		val userUUID = "myUserUUID"
		val user = UserEntity("김항해", userUUID, 20000)
		userJpaRepository.save(user)

		val seat = ConcertSeatEntity(99, 15000, 2L, expiredAt)
		concertSeatJpaRepository.save(seat)

		val reservation = ReservationEntity(expiredAt, 15000, user.id, 1L, 2L, seat.id)
		reservationJpaRepository.save(reservation)

		val tokenUUID = "myTokenUUID"

		val cri = PaymentCri.Create(userUUID, tokenUUID, reservation.id)

		// when
		sut.payReservation(cri)

		//then
		val actualUser = userJpaRepository.findByIdOrNull(user.id)!!
		assertThat(actualUser.balance).isEqualTo(5000)

		val actualPointHistories = pointHistoryJpaRepository.findAll().filter { it.userId == actualUser.id }
		assertThat(actualPointHistories).hasSize(1)
			.anyMatch { it.type == PointHistoryType.USE && it.amount == reservation.price }

		val actualPayments = paymentJpaRepository.findAll().filter { it.userId == actualUser.id }
		assertThat(actualPayments).hasSize(1)
			.anyMatch { it.price == reservation.price && it.reservationId == reservation.id }

		val actualReservation = reservationJpaRepository.findByIdOrNull(reservation.id)!!
		assertThat(actualReservation.expiredAt).isNull()

		val actualSeat = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actualSeat.reservedUntil).isNull()
	}
}

@TestConfiguration
class ReservationFacadeIntegrationTestConfig {
	@Bean
	@Primary
	fun clockHolder(): ClockHolder = ClockHolder {
		LocalDateTime.of(2025, 1, 17, 12, 59, 59)
	}
}
