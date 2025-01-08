package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.domain.concert.ConcertSchedule
import kr.hhplus.be.server.domain.concert.ConcertSeat
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class ConcertFacadeServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ConcertFacadeService,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `콘서트 조회 시, 여러 콘서트 중 현재 진행 중인 콘서트들의 목록을 반환한다`() {
		// given
		val concert1 = Concert("콘서트1", "가수1", true)
		val concert2 = Concert("콘서트2", "가수2", true)
		val concert3 = Concert("콘서트3", "가수3", false)
		val concert4 = Concert("콘서트4", "가수4", false)
		val concert5 = Concert("콘서트5", "가수5", false)
		concertJpaRepository.saveAll(listOf(concert1, concert2, concert3, concert4, concert5))

		// when
		val actual = sut.getConcertInformation()

		//then
		assertThat(actual).hasSize(3)
			.noneMatch { it.concertId == concert1.id || it.title == "콘서트1" || it.provider == "가수1" }
			.noneMatch { it.concertId == concert2.id || it.title == "콘서트2" || it.provider == "가수2" }
			.anyMatch { it.concertId == concert3.id && it.title == "콘서트3" && it.provider == "가수3" }
			.anyMatch { it.concertId == concert4.id && it.title == "콘서트4" && it.provider == "가수4" }
			.anyMatch { it.concertId == concert5.id && it.title == "콘서트5" && it.provider == "가수5" }
	}

	@Test
	fun `콘서트 일정 조회 시, 여러 콘서트 중 concertId가 일치하는 콘서트의 일정 정보만 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 17, 55, 50)
		val concert1 = Concert("콘서트1", "가수1", false)
		val concert2 = Concert("콘서트2", "가수2", false)
		concertJpaRepository.saveAll(listOf(concert1, concert2))

		val schedule1 = ConcertSchedule(50, testTime, testTime.plusHours(1), concert1.id)
		val schedule2 = ConcertSchedule(50, testTime, testTime.plusHours(2), concert2.id)
		val schedule3 = ConcertSchedule(50, testTime, testTime.plusHours(3), concert1.id)
		val schedule4 = ConcertSchedule(50, testTime, testTime.plusHours(4), concert2.id)
		val schedule5 = ConcertSchedule(50, testTime, testTime.plusHours(5), concert2.id)
		concertScheduleJpaRepository.saveAll(listOf(schedule1, schedule2, schedule3, schedule4, schedule5))

		// when
		val actual = sut.getConcertScheduleInformation(concert2.id)

		//then
		assertThat(actual).hasSize(3)
			.noneMatch { it.concertId == concert1.id }
			.allMatch { it.concertId == concert2.id }

			.noneMatch { it.concertScheduleId == schedule1.id }
			.noneMatch { it.concertScheduleId == schedule3.id }
			.anyMatch { it.concertScheduleId == schedule2.id }
			.anyMatch { it.concertScheduleId == schedule4.id }
			.anyMatch { it.concertScheduleId == schedule5.id }
	}

	@Test
	fun `콘서트 좌석 조회 시, 여러 콘서트 일정 중 concertScheduleId가 일치하는 콘서트의 좌석 정보만 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 17, 55, 50)
		val schedule1 = ConcertSchedule(50, testTime, testTime.plusHours(1), 101L)
		val schedule2 = ConcertSchedule(50, testTime, testTime.plusHours(2), 101L)
		concertScheduleJpaRepository.saveAll(listOf(schedule1, schedule2))

		val seat1 = ConcertSeat(1, 15000, schedule1.id)
		val seat2 = ConcertSeat(2, 15000, schedule2.id)
		val seat3 = ConcertSeat(3, 15000, schedule2.id)
		val seat4 = ConcertSeat(4, 15000, schedule1.id)
		val seat5 = ConcertSeat(5, 15000, schedule2.id)
		concertSeatJpaRepository.saveAll(listOf(seat1, seat2, seat3, seat4, seat5))

		// when
		val actual = sut.getConcertSeatInformation(101L, schedule1.id)

		//then
		assertThat(actual).hasSize(2)
			.allMatch { it.concertId == 101L }

			.allMatch { it.concertScheduleId == schedule1.id }
			.noneMatch { it.concertScheduleId == schedule2.id }

			.noneMatch { it.seatId == seat2.id }
			.noneMatch { it.seatId == seat3.id }
			.noneMatch { it.seatId == seat5.id }
			.anyMatch { it.seatId == seat1.id }
			.anyMatch { it.seatId == seat4.id }
	}
}