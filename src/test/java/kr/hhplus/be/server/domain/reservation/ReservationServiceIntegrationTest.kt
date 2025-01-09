package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@SpringBootTest
class ReservationServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ReservationService,
	@Autowired private val reservationJpaRepository: ReservationJpaRepository
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `결제를 위해 예약 조회 요청 시, userId와 reservationId에 맞는 예약을 조회해서 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 30, 55)
		val userId = 1L
		val reservation = Reservation(testTime.plusMinutes(5), 1000, userId, 2L, 3L, 4L)
		reservationJpaRepository.save(reservation)

		// when
		val actual = sut.getReservationForPay(userId, reservation.id) { testTime }

		//then
		assertThat(actual.expiredAt).isEqualTo(testTime.plusMinutes(5))
		assertThat(actual.price).isEqualTo(1000)
		assertThat(actual.userId).isEqualTo(1L)
		assertThat(actual.concertId).isEqualTo(2L)
		assertThat(actual.concertScheduleId).isEqualTo(3L)
		assertThat(actual.concertSeatId).isEqualTo(4L)
	}

	@Test
	fun `예약 요청 시, 예약이 없다면 새로운 Reservation을 생성하고 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 19, 43, 31)
		val request = ReservationCommand.Create(1000, 1L, 2L, 3L, 4L)

		// when
		val actual = sut.reserve(request) { testTime }

		//then
		val set = reservationJpaRepository.findByIdOrNull(actual.id)!!

		assertThat(set.price).isEqualTo(1000)
		assertThat(set.userId).isEqualTo(1L)
		assertThat(set.concertId).isEqualTo(2L)
		assertThat(set.concertScheduleId).isEqualTo(3L)
		assertThat(set.concertSeatId).isEqualTo(4L)
		assertThat(set.expiredAt).isEqualTo(testTime.plusMinutes(5))
	}

	@Test
	fun `예약 요청 시, 이미 예약되었지만 만료된 상태라면 Reservation을 생성하고 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 19, 43, 31)
		val request = ReservationCommand.Create(1000, 1L, 2L, 3L, 4L)

		val reservation = Reservation(testTime.minusNanos(1000), 800, 11L, 2L, 3L, 4L)
		reservationJpaRepository.save(reservation)

		// when
		val actual = sut.reserve(request) { testTime }

		//then
		val set = reservationJpaRepository.findByIdOrNull(actual.id)!!

		assertThat(set.price).isEqualTo(1000)
		assertThat(set.userId).isEqualTo(1L)
		assertThat(set.concertId).isEqualTo(2L)
		assertThat(set.concertScheduleId).isEqualTo(3L)
		assertThat(set.concertSeatId).isEqualTo(4L)
		assertThat(set.expiredAt).isEqualTo(testTime.plusMinutes(5))
	}
}