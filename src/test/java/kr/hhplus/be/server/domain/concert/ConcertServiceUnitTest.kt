package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.domain.KSelect.Companion.field
import kr.hhplus.be.server.infrastructure.exception.EntityNotFoundException
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
		val concert = createConcert(concertId, "콘서트1", "가수1", schedules)

		`when`(concertRepository.findConcertWithSchedule(concertId))
			.then { concert }

		// when
		val actual = sut.getConcertSchedule(concertId)

		//then
		verify(concertRepository).findConcertWithSchedule(13L)

		assertThat(actual).hasSize(3)
			.allMatch { it.concertId == 13L }
			.anyMatch { it.concertScheduleId == 1L }
			.anyMatch { it.concertScheduleId == 2L }
			.anyMatch { it.concertScheduleId == 3L }
	}

	@Test
	fun `콘서트 일정 조회 시, 없는 콘서트 id로 조회하면 EntityNotFoundException이 발생한다`() {
		// given
		val concertId = 201L
		`when`(concertRepository.findConcertWithSchedule(concertId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getConcertSchedule(concertId) }
			.isInstanceOf(EntityNotFoundException::class.java)
			.hasMessage("Concert 엔티티를 찾을 수 없습니다. Id=201")
	}

	@Test
	fun `콘서트 좌석 조회 시, 콘서트 일정 id에 해당하는 좌석을 조회하고 그 결과를 반환한다`() {
		// given
		val scheduleId = 123L

		val seat1 = createSeat(101L, scheduleId, 1)
		val seat2 = createSeat(102L, scheduleId, 2)
		val seat3 = createSeat(103L, scheduleId, 3)
		val seats = listOf(seat1, seat2, seat3)
		val schedule = createSchedule(scheduleId, 13L, seats)

		`when`(concertRepository.findScheduleWithSeat(scheduleId))
			.then { schedule }

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
	fun `콘서트 좌석 조회 시, 없는 콘서트 일정 id로 조회하면 EntityNotFoundException이 발생한다`() {
		// given
		val concertScheduleId = 202L
		`when`(concertRepository.findScheduleWithSeat(concertScheduleId))
			.then { null }

		// when then
		assertThatThrownBy { sut.getConcertSeat(1L, concertScheduleId) }
			.isInstanceOf(EntityNotFoundException::class.java)
			.hasMessage("ConcertSchedule 엔티티를 찾을 수 없습니다. Id=202")
	}

	private fun createConcert(id: Long, title: String, provider: String, schedules: List<ConcertSchedule> = listOf()): Concert =
		Instancio.of(Concert::class.java)
			.set(field(Concert::id), id)
			.set(field(Concert::title), title)
			.set(field(Concert::provider), provider)
			.set(field(Concert::finished), false)
			.set(field(Concert::concertSchedules), schedules)
			.create()

	private fun createSchedule(id: Long, concertId: Long, seats: List<ConcertSeat> = listOf()): ConcertSchedule =
		Instancio.of(ConcertSchedule::class.java)
			.set(field(ConcertSchedule::id), id)
			.set(field(ConcertSchedule::concertId), concertId)
			.set(field(ConcertSchedule::concertSeats), seats)
			.create()

	private fun createSeat(id: Long, scheduleId: Long, seatNumber: Int): ConcertSeat =
		Instancio.of(ConcertSeat::class.java)
			.set(field(ConcertSeat::id), id)
			.set(field(ConcertSeat::concertScheduleId), scheduleId)
			.set(field(ConcertSeat::seatNumber), seatNumber)
			.create()
}