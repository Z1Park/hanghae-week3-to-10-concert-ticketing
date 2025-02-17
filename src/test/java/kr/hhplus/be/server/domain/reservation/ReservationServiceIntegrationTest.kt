package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.application.concert.ConcertApiClient
import kr.hhplus.be.server.application.user.UserApiClient
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
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
	@Autowired private val reservationJpaRepository: ReservationJpaRepository,
	@Autowired private val concertApiClient: ConcertApiClient,
	@Autowired private val userApiClient: UserApiClient,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `결제를 위해 예약 조회 요청 시, reservationId에 맞는 예약을 조회해서 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 30, 55)
		val reservation = ReservationEntity(testTime.plusMinutes(5), 1000, 1L, 2L, 3L, 4L)
		reservationJpaRepository.save(reservation)

		// when
		val actual = sut.getReservationForPay(reservation.id) { testTime }

		//then
		assertThat(actual.expiredAt).isEqualTo(testTime.plusMinutes(5))
		assertThat(actual.price).isEqualTo(1000)
		assertThat(actual.userId).isEqualTo(1L)
		assertThat(actual.concertId).isEqualTo(2L)
		assertThat(actual.concertScheduleId).isEqualTo(3L)
		assertThat(actual.concertSeatId).isEqualTo(4L)
	}

	@Test
	fun `예약 롤백 시, reservationId를 통해 저장된 예약을 조회 후 삭제한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 9, 36, 5)

		val reservation = ReservationEntity(null, 1000, 1L, 2L, 3L, 4L)
		reservationJpaRepository.save(reservation)

		// when
		sut.rollbackReservation(reservation.id, testTime)

		//then
		val actual = reservationJpaRepository.findByIdOrNull(reservation.id)!!
		assertThat(actual.expiredAt).isEqualTo(testTime)
	}
}