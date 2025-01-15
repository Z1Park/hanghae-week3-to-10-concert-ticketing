package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.domain.KSelect.Companion.field
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

@ExtendWith(MockitoExtension::class)
class ConcertServiceUnitTest {

	@InjectMocks
	private lateinit var sut: ConcertService

	@Mock
	private lateinit var concertRepository: ConcertRepository

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
			.anyMatch { it.concertId == 1L && it.title == "콘서트1" && it.provider == "가수1" }
			.anyMatch { it.concertId == 2L && it.title == "콘서트2" && it.provider == "그룹1" }
			.anyMatch { it.concertId == 3L && it.title == "콘서트3" && it.provider == "가수2" }
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
	fun `좌석 총정보 조회 시, 콘서트-일정-좌석을 전부 조회 후 모든 정보를 취합하여 반환한다`() {
		// given
		val command = ConcertCommand.Total(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 1L)
		val seat = createSeat(3L, 2L, 123)

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when
		val actual = sut.getConcertSeatDetailInformation(command)

		//then
		assertThat(actual.concertId).isEqualTo(1L)
		assertThat(actual.concertScheduleId).isEqualTo(2L)
		assertThat(actual.seatId).isEqualTo(3L)
		assertThat(actual.seatNumber).isEqualTo(123)
	}

	@Test
	fun `좌석 총정보 조회 시, 서로 연관되지 않은 콘서트-일정으로 요청하면 CustomException이 발생한다`() {
		// given
		val command = ConcertCommand.Total(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 5L)
		val seat = createSeat(3L, 2L, 123)

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when then
		assertThatThrownBy { sut.getConcertSeatDetailInformation(command) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_MATCH_SCHEDULE)
	}

	@Test
	fun `좌석 총정보 조회 시, 서로 연관되지 않은 일정-좌석으로 요청하면 CustomException이 발생한다`() {
		// given
		val command = ConcertCommand.Total(1L, 2L, 3L)
		val concert = createConcert(1L)
		val schedule = createSchedule(2L, 1L)
		val seat = createSeat(3L, 7L, 123)

		`when`(concertRepository.findConcert(1L)).then { concert }
		`when`(concertRepository.findSchedule(2L)).then { schedule }
		`when`(concertRepository.findSeat(3L)).then { seat }

		// when then
		assertThatThrownBy { sut.getConcertSeatDetailInformation(command) }
			.isInstanceOf(CustomException::class.java)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.NOT_MATCH_SEAT)
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