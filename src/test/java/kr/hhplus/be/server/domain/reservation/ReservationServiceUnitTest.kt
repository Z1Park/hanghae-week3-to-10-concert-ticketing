package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.domain.KSelect.Companion.field
import kr.hhplus.be.server.domain.exception.AlreadyReservedException
import kr.hhplus.be.server.domain.exception.ReservationExpiredException
import org.apache.coyote.BadRequestException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.instancio.Instancio
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ReservationServiceUnitTest {

	@InjectMocks
	private lateinit var sut: ReservationService

	@Mock
	private lateinit var reservationRepository: ReservationRepository

	@Test
	fun `결제를 위해 예약 정보 조회 시, 유저 id와 예약 id를 통해 조회하고 예약 내용을 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 13, 53)
		val userId = 94L
		val reservationId = 19L
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::id), reservationId)
			.set(field(Reservation::userId), userId)
			.set(field(Reservation::expiredAt), testTime.plusMinutes(10))
			.create()

		`when`(reservationRepository.findByUserIdAndReservationId(userId, reservationId))
			.then { reservation }

		// when
		val actual = sut.getReservationForPay(userId, reservationId) { testTime }

		//then
		verify(reservationRepository).findByUserIdAndReservationId(userId, reservationId)

		assertThat(actual.id).isEqualTo(reservationId)
		assertThat(actual.userId).isEqualTo(userId)
	}

	@Test
	fun `결제를 위해 예약 정보 조회 시, 유저 id와 예약된 예약 id가 맞지 않는다면 BadRequestException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 13, 53)
		val userId = 94L
		val reservationId = 19L

		`when`(reservationRepository.findByUserIdAndReservationId(userId, reservationId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getReservationForPay(userId, reservationId) { testTime } }
			.isInstanceOf(BadRequestException::class.java)
	}

	@Test
	fun `결제를 위해 예약 정보 조회 시, 예약이 만료되었다면 ReservationExpiredException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 13, 53)
		val userId = 94L
		val reservationId = 19L
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::id), reservationId)
			.set(field(Reservation::userId), userId)
			.set(field(Reservation::expiredAt), testTime.minusNanos(1000))
			.create()

		`when`(reservationRepository.findByUserIdAndReservationId(userId, reservationId))
			.then { reservation }

		// when then
		assertThatThrownBy { sut.getReservationForPay(userId, reservationId) { testTime } }
			.isInstanceOf(ReservationExpiredException::class.java)
			.hasMessage("이미 만료된 예약입니다.")
	}

	@Test
	fun `예약 요청 시, 좌석의 예약 정보가 있는지 확인 후 새로 예약을 만들어 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 1, 12, 2)
		val request = ReservationCommand.Create(780, 1L, 2L, 3L, 4L)
		val reservation = request.toReservation(testTime.plusMinutes(5))

		`when`(reservationRepository.findByScheduleAndSeatForUpdate(3L, 4L))
			.then { null }
		`when`(reservationRepository.save(reservation))
			.then { reservation }

		// when
		val actual = sut.reserve(request) { testTime }

		//then
		verify(reservationRepository).findByScheduleAndSeatForUpdate(3L, 4L)
		verify(reservationRepository).save(reservation)

		assertThat(actual.price).isEqualTo(780)
		assertThat(actual.userId).isEqualTo(1L)
		assertThat(actual.concertId).isEqualTo(2L)
		assertThat(actual.concertScheduleId).isEqualTo(3L)
		assertThat(actual.concertSeatId).isEqualTo(4L)
		assertThat(actual.expiredAt).isEqualTo(testTime.plusMinutes(5))
	}

	@Test
	fun `예약 요청 시, 이미 예약된 자리라면 AlreadyReservedException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 1, 12, 2)
		val request = ReservationCommand.Create(600, 1L, 2L, 3L, 4L)
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::expiredAt), testTime.plusNanos(1000))
			.create()

		`when`(reservationRepository.findByScheduleAndSeatForUpdate(3L, 4L))
			.then { reservation }

		// when then
		assertThatThrownBy { sut.reserve(request) { testTime } }
			.isInstanceOf(AlreadyReservedException::class.java)
			.hasMessage("이미 선택된 좌석입니다.")
	}

	@Test
	fun `예약 요청 시, 이미 예약된 자리더라도 예약이 만료되었다면 새로 예약을 만들어 저장한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 1, 12, 2)
		val request = ReservationCommand.Create(1000, 1L, 2L, 3L, 4L)
		val reservation = request.toReservation(testTime.plusMinutes(5))
		val existReservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::expiredAt), testTime.minusNanos(1000))
			.create()

		`when`(reservationRepository.findByScheduleAndSeatForUpdate(3L, 4L))
			.then { existReservation }
		`when`(reservationRepository.save(reservation))
			.then { reservation }

		// when
		val actual = sut.reserve(request) { testTime }

		//then
		verify(reservationRepository).findByScheduleAndSeatForUpdate(3L, 4L)
		verify(reservationRepository).save(reservation)

		assertThat(actual.userId).isEqualTo(1L)
		assertThat(actual.concertId).isEqualTo(2L)
		assertThat(actual.concertScheduleId).isEqualTo(3L)
		assertThat(actual.concertSeatId).isEqualTo(4L)
		assertThat(actual.expiredAt).isEqualTo(testTime.plusMinutes(5))
	}
}