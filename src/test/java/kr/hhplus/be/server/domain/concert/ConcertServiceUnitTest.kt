package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.KSelect.Companion.field
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.concert.model.Concert
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule
import kr.hhplus.be.server.domain.concert.model.ConcertSeat
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
class ConcertServiceUnitTest {

	@InjectMocks
	private lateinit var sut: ConcertService

	@Mock
	private lateinit var concertRepository: ConcertRepository

	@Mock
	private lateinit var applicationEventPublisher: ApplicationEventPublisher

	@Test
	fun `콘서트 정보 조회 시, 현재 진행 중인 콘서트 정보를 조회하고 그 결과를 반환한다`() {
		// given
		val concert1 = createConcert(1L, "콘서트1", "가수1")
		val concert2 = createConcert(2L, "콘서트2", "그룹1")
		val concert3 = createConcert(3L, "콘서트3", "가수2")
		val concerts = listOf(concert1, concert2, concert3)

		`when`(concertRepository.findAllConcert(false)).then { concerts }

		// when
		val actual = sut.getConcert()

		//then
		verify(concertRepository).findAllConcert(false)

		assertThat(actual).hasSize(3)
			.anyMatch { it.id == 1L && it.title == "콘서트1" && it.provider == "가수1" }
			.anyMatch { it.id == 2L && it.title == "콘서트2" && it.provider == "그룹1" }
			.anyMatch { it.id == 3L && it.title == "콘서트3" && it.provider == "가수2" }
	}

	@Test
	fun `콘서트 일정 조회 시, 콘서트 id에 해당하는 콘서트 일정을 조회하고 그 결과를 반환한다`() {
		// given
		val concertId = 13L

		val schedule1 = createSchedule(1L, concertId)
		val schedule2 = createSchedule(2L, concertId)
		val schedule3 = createSchedule(3L, concertId)
		val schedules = listOf(schedule1, schedule2, schedule3)
		val concert = createConcert(concertId, "콘서트1", "가수1")

		`when`(concertRepository.findConcert(concertId))
			.then { concert }
		`when`(concertRepository.findAllScheduleByConcertId(concertId))
			.then { schedules }

		// when
		val actual = sut.getConcertSchedule(concertId)

		//then
		assertThat(actual).hasSize(3)
			.allMatch { it.concertId == 13L }
			.anyMatch { it.concertScheduleId == 1L }
			.anyMatch { it.concertScheduleId == 2L }
			.anyMatch { it.concertScheduleId == 3L }
	}

	@Test
	fun `콘서트 일정 조회 시, 없는 콘서트 id로 조회하면 CustomException이 발생한다`() {
		// given
		val concertId = 201L
		`when`(concertRepository.findConcert(concertId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getConcertSchedule(concertId) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `콘서트 좌석 조회 시, 콘서트 일정 id에 해당하는 좌석을 조회하고 그 결과를 반환한다`() {
		// given
		val scheduleId = 123L

		val seat1 = createSeat(101L, scheduleId, 1)
		val seat2 = createSeat(102L, scheduleId, 2)
		val seat3 = createSeat(103L, scheduleId, 3)
		val seats = listOf(seat1, seat2, seat3)
		val schedule = createSchedule(scheduleId, 13L)

		`when`(concertRepository.findSchedule(scheduleId))
			.then { schedule }
		`when`(concertRepository.findAllSeatByConcertScheduleId(scheduleId))
			.then { seats }

		// when
		val actual = sut.getConcertSeat(1L, scheduleId)

		//then
		assertThat(actual).hasSize(3)
			.allMatch { it.concertScheduleId == scheduleId }
			.anyMatch { it.seatId == 101L && it.seatNumber == 1 }
			.anyMatch { it.seatId == 102L && it.seatNumber == 2 }
			.anyMatch { it.seatId == 103L && it.seatNumber == 3 }
	}

	@Test
	fun `콘서트 좌석 조회 시, 없는 콘서트 일정 id로 조회하면 CustomException이 발생한다`() {
		// given
		val concertScheduleId = 202L
		`when`(concertRepository.findSchedule(concertScheduleId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getConcertSeat(1L, concertScheduleId) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `좌석 선점 시, 5분 후까지 해당 좌석을 선점한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val command = ConcertCommand.Preoccupy(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 1L)
		val seat = Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), 3L)
			.set(field(ConcertSeat::concertScheduleId), 2L)
			.set(field(ConcertSeat::price), 2500)
			.set(field(ConcertSeat::reservedUntil), testTime.minusNanos(1000))
			.create()

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when
		sut.preoccupyConcertSeat(command, "thisistraceid") { testTime }

		//then
		val expiredAt = testTime.plusMinutes(5)
		assertThat(seat.reservedUntil).isEqualTo(expiredAt)
	}

	@Test
	fun `좌석 선점 시, 서로 연관되지 않은 콘서트-일정으로 요청하면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val command = ConcertCommand.Preoccupy(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 5L)
		val seat = createSeat(3L, 2L, 123)

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when then
		assertThatThrownBy { sut.preoccupyConcertSeat(command, "thisistraceid") { testTime } }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_MATCH_SCHEDULE)
	}

	@Test
	fun `좌석 선점 시, 서로 연관되지 않은 일정-좌석으로 요청하면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val command = ConcertCommand.Preoccupy(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 1L)
		val seat = createSeat(3L, 7L, 123)

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when then
		assertThatThrownBy { sut.preoccupyConcertSeat(command, "thisistraceid") { testTime } }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_MATCH_SEAT)
	}

	@Test
	fun `좌석 선점 시, 이미 예약된 좌석이라면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val command = ConcertCommand.Preoccupy(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 1L)
		val seat = Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), 3L)
			.set(field(ConcertSeat::concertScheduleId), 2L)
			.set(field(ConcertSeat::reservedUntil), testTime.plusNanos(1000))
			.create()

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when then
		assertThatThrownBy { sut.preoccupyConcertSeat(command, "thisistraceid") { testTime } }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ALREADY_RESERVED)
	}

	@Test
	fun `좌석 선점 롤백 시, 원래의 reservedUntill 값을 원래의 데이터로 복구한다`() {
		// given
		val seatId = 12L
		val testTime = LocalDateTime.of(2025, 2, 5, 11, 25, 31)
		val expiredAt = testTime.minusSeconds(3)
		val seat = Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), seatId)
			.set(field(ConcertSeat::reservedUntil), testTime.plusMinutes(5))
			.create()

		`when`(concertRepository.findSeat(seatId))
			.then { seat }

		// when
		sut.rollbackPreoccupyConcertSeat(seatId, expiredAt)

		//then
		assertThat(seat.reservedUntil).isEqualTo(expiredAt)
	}

	@Test
	fun `좌석 선점 롤백 시, 잘못된 Id로 조회하면 CustomException이 발생한다`() {
		// given
		val expiredAt = LocalDateTime.of(2025, 2, 5, 11, 23, 59)

		// when then
		assertThatThrownBy { sut.rollbackPreoccupyConcertSeat(1L, expiredAt) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `좌석 매진 요청 시, 좌석의 만료 기한을 null로 바꾼다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val concertSeatId = 1L
		val seat = Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), concertSeatId)
			.set(field(ConcertSeat::reservedUntil), testTime)
			.create()

		`when`(concertRepository.findSeat(concertSeatId))
			.then { seat }

		// when
		sut.makeSoldOutConcertSeat(concertSeatId, "thisistraceid")

		//then
		assertThat(seat.reservedUntil).isNull()

		verify(concertRepository).save(seat)
	}

	@Test
	fun `좌석 매진 요청 시, 잘못된 seatId를 통해 조회하면 CustomException이 발생한다`() {
		// given
		val concertSeatId = 1L

		`when`(concertRepository.findSeat(concertSeatId))
			.then { null }

		// when  then
		assertThatThrownBy { sut.makeSoldOutConcertSeat(concertSeatId, "thisistraceid") }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	@Test
	fun `좌석 매진 롤백 시, 좌석의 만료 기한을 주어진 기한으로 바꾼다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val concertSeatId = 1L
		val seat = Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), concertSeatId)
			.set(field(ConcertSeat::reservedUntil), null)
			.create()

		`when`(concertRepository.findSeat(concertSeatId))
			.then { seat }

		// when
		sut.rollbackSoldOutedConcertSeat(concertSeatId, testTime)

		//then
		assertThat(seat.reservedUntil).isEqualTo(testTime)

		verify(concertRepository).save(seat)
	}

	@Test
	fun `좌석 매진 롤백 시, 잘못된 seatId를 통해 조회하면 CustomException이 발생한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 16, 1, 5, 36)
		val concertSeatId = 1L

		`when`(concertRepository.findSeat(concertSeatId))
			.then { null }

		// when  then
		assertThatThrownBy { sut.rollbackSoldOutedConcertSeat(concertSeatId, testTime) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ENTITY_NOT_FOUND)
	}

	private fun createConcert(
		id: Long,
		title: String = "김항해",
		provider: String = "항해플러스",
	): Concert =
		Instancio.of(Concert::class.java)
			.set(field(Concert::id), id)
			.set(field(Concert::title), title)
			.set(field(Concert::provider), provider)
			.set(field(Concert::finished), false)
			.create()

	private fun createSchedule(id: Long, concertId: Long): ConcertSchedule =
		Instancio.of(ConcertSchedule::class.java)
			.set(field(ConcertSchedule::id), id)
			.set(field(ConcertSchedule::concertId), concertId)
			.create()

	private fun createSeat(id: Long, scheduleId: Long, seatNumber: Int): ConcertSeat =
		Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), id)
			.set(field(ConcertSeat::concertScheduleId), scheduleId)
			.set(field(ConcertSeat::seatNumber), seatNumber)
			.create()
}