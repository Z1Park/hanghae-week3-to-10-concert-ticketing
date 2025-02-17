package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.KSelect.Companion.field
import kr.hhplus.be.server.application.concert.ConcertApiClient
import kr.hhplus.be.server.application.user.UserApiClient
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.concert.ConcertCommand
import kr.hhplus.be.server.domain.concert.ConcertInfo
import kr.hhplus.be.server.domain.reservation.model.Reservation
import kr.hhplus.be.server.domain.user.UserInfo
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
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ReservationServiceUnitTest {

	@InjectMocks
	private lateinit var sut: ReservationService

	@Mock
	private lateinit var reservationRepository: ReservationRepository

	@Mock
	private lateinit var concertApiClient: ConcertApiClient

	@Mock
	private lateinit var userApiClient: UserApiClient

	@Mock
	private lateinit var applicationEventPublisher: ApplicationEventPublisher

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

		`when`(reservationRepository.findById(reservationId))
			.then { reservation }

		// when
		val actual = sut.getReservationForPay(reservationId) { testTime }

		//then
		verify(reservationRepository).findById(reservationId)

		assertThat(actual.id).isEqualTo(reservationId)
		assertThat(actual.userId).isEqualTo(userId)
	}

	@Test
	fun `결제를 위해 예약 정보 조회 시, 유저 id와 예약된 예약 id가 맞지 않는다면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 13, 53)
		val reservationId = 19L

		`when`(reservationRepository.findById(reservationId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getReservationForPay(reservationId) { testTime } }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `결제를 위해 예약 정보 조회 시, 예약이 만료되었다면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 12, 13, 53)
		val reservationId = 19L
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::id), reservationId)
			.set(field(Reservation::expiredAt), testTime.minusNanos(1000))
			.create()

		`when`(reservationRepository.findById(reservationId))
			.then { reservation }

		// when then
		assertThatThrownBy { sut.getReservationForPay(reservationId) { testTime } }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.EXPIRED_RESERVATION)
	}
	
	@Test
	fun `결제 완료 시, 예약의 expiredAt이 null이 되고 저장하는 메서드를 호출한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 10, 7, 34, 6)

		val reservationId = 13L
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::id), reservationId)
			.set(field(Reservation::expiredAt), testTime.plusMinutes(10))
			.create()

		`when`(reservationRepository.findById(reservationId))
			.then { reservation }

		// when
		sut.makeSoldOut(reservationId)

		//then
		verify(reservationRepository).save(reservation)

		assertThat(reservation.expiredAt).isNull()
	}

	@Test
	fun `예약 롤백 시, reservationId에 따라 조회 후 만료 시간을 롤백한다`() {
		// given
		val reservationId = 74L
		val testTime = LocalDateTime.of(2025, 1, 16, 9, 36, 5)
		val reservation = Instancio.of(Reservation::class.java)
			.set(field(Reservation::expiredAt), null)
			.create()

		`when`(reservationRepository.findById(reservationId))
			.then { reservation }

		// when
		sut.rollbackReservation(reservationId, testTime)

		//then
		assertThat(reservation.expiredAt).isEqualTo(testTime)

		verify(reservationRepository).save(reservation)
	}

	@Test
	fun `예약 롤백 시, 없는 reservationId라면 CustomException이 발생한다`() {
		// given
		val reservationId = 74L
		val testTime = LocalDateTime.of(2025, 1, 16, 9, 36, 5)

		`when`(reservationRepository.findById(reservationId))
			.then { null }

		// when then
		assertThatThrownBy { sut.rollbackReservation(reservationId, testTime) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `전날 예약 조회 시, testTime의 전날의 00시 00분 00초부터 testTime의 00시 00분 00초까지를 조회한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 7, 3, 58, 3)

		// when
		sut.getYesterdayReservationConcertCounts() { testTime }

		//then
		val expectedEnd = testTime.toLocalDate().atStartOfDay()
		val expectedStart = expectedEnd.minusDays(1)
		verify(reservationRepository).findTopReservationConcertIdsByCreatedAtBetween(expectedStart, expectedEnd, 20)
	}
}