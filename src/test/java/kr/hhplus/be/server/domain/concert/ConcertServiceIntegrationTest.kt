package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@SpringBootTest
class ConcertServiceIntegrationTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ConcertService,
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
		val concert1 = ConcertEntity("콘서트1", "가수1", true)
		val concert2 = ConcertEntity("콘서트2", "가수2", true)
		val concert3 = ConcertEntity("콘서트3", "가수3", false)
		val concert4 = ConcertEntity("콘서트4", "가수4", false)
		val concert5 = ConcertEntity("콘서트5", "가수5", false)
		concertJpaRepository.saveAll(listOf(concert1, concert2, concert3, concert4, concert5))

		// when
		val actual = sut.getConcert()

		//then
		assertThat(actual).hasSize(3)
			.noneMatch { it.id == concert1.id || it.title == "콘서트1" || it.provider == "가수1" }
			.noneMatch { it.id == concert2.id || it.title == "콘서트2" || it.provider == "가수2" }
			.anyMatch { it.id == concert3.id && it.title == "콘서트3" && it.provider == "가수3" }
			.anyMatch { it.id == concert4.id && it.title == "콘서트4" && it.provider == "가수4" }
			.anyMatch { it.id == concert5.id && it.title == "콘서트5" && it.provider == "가수5" }
	}

	@Test
	fun `콘서트 일정 조회 시, 여러 콘서트 중 concertId가 일치하는 콘서트의 일정 정보만 반환한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 8, 17, 55, 50)
		val concert1 = ConcertEntity("콘서트1", "가수1", false)
		val concert2 = ConcertEntity("콘서트2", "가수2", false)
		concertJpaRepository.saveAll(listOf(concert1, concert2))

		val schedule1 = ConcertScheduleEntity(50, testTime, testTime.plusHours(1), concert1.id)
		val schedule2 = ConcertScheduleEntity(50, testTime, testTime.plusHours(2), concert2.id)
		val schedule3 = ConcertScheduleEntity(50, testTime, testTime.plusHours(3), concert1.id)
		val schedule4 = ConcertScheduleEntity(50, testTime, testTime.plusHours(4), concert2.id)
		val schedule5 = ConcertScheduleEntity(50, testTime, testTime.plusHours(5), concert2.id)
		concertScheduleJpaRepository.saveAll(listOf(schedule1, schedule2, schedule3, schedule4, schedule5))

		// when
		val actual = sut.getConcertSchedule(concert2.id)

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
		val schedule1 = ConcertScheduleEntity(50, testTime, testTime.plusHours(1), 101L)
		val schedule2 = ConcertScheduleEntity(50, testTime, testTime.plusHours(2), 101L)
		concertScheduleJpaRepository.saveAll(listOf(schedule1, schedule2))

		val seat1 = ConcertSeatEntity(1, 15000, schedule1.id, testTime)
		val seat2 = ConcertSeatEntity(2, 15000, schedule2.id, testTime)
		val seat3 = ConcertSeatEntity(3, 15000, schedule2.id, testTime)
		val seat4 = ConcertSeatEntity(4, 15000, schedule1.id, testTime)
		val seat5 = ConcertSeatEntity(5, 15000, schedule2.id, testTime)
		concertSeatJpaRepository.saveAll(listOf(seat1, seat2, seat3, seat4, seat5))

		// when
		val actual = sut.getConcertSeat(101L, schedule1.id)

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

	@Test
	fun `좌석 선점 시, 5분 후 까지 해당 좌석을 선점한다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 22, 11, 37)

		val concert = ConcertEntity("항해콘", "김항해")
		concertJpaRepository.save(concert)

		val schedule1 = ConcertScheduleEntity(50, testTime, testTime.plusHours(3), concert.id)
		val schedule2 = ConcertScheduleEntity(50, testTime, testTime.plusHours(5), concert.id)
		concertScheduleJpaRepository.saveAll(listOf(schedule1, schedule2))

		val seat1 = ConcertSeatEntity(12, 15000, schedule1.id, testTime.minusMinutes(1))
		val seat2 = ConcertSeatEntity(13, 16000, schedule1.id, testTime.minusMinutes(1))
		val seat3 = ConcertSeatEntity(14, 17000, schedule2.id, testTime.minusMinutes(1))
		val seat4 = ConcertSeatEntity(15, 18000, schedule2.id, testTime.minusMinutes(1))
		concertSeatJpaRepository.saveAll(listOf(seat1, seat2, seat3, seat4))

		val query = ConcertCommand.Reserve(concert.id, schedule2.id, seat3.id, 1L)

		// when
		sut.preoccupyConcertSeat(query) { testTime }

		//then
		val actual = concertSeatJpaRepository.findByIdOrNull(seat3.id)!!
		val expiredAt = testTime.plusMinutes(5)
		assertThat(actual.reservedUntil).isEqualTo(expiredAt)
	}

	@Test
	fun `좌석 선점 롤백 시, 해당 좌석의 만료 기한이 파라미터로 주어지는 원래의 만료기한으로 변경된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 2, 5, 11, 30, 37)
		val seat = ConcertSeatEntity(100, 15000, 3L, testTime.plusMinutes(5))
		concertSeatJpaRepository.save(seat)

		// when
		sut.rollbackPreoccupyConcertSeat(seat.id, testTime)

		//then
		val actual = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actual.reservedUntil).isEqualTo(testTime)
	}

	@Test
	fun `좌석 매진 요청 시, 해당 좌석의 만료 기한에 null이 저장된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 22, 11, 37)
		val seat = ConcertSeatEntity(12, 15000, 3L, testTime)
		concertSeatJpaRepository.save(seat)

		// when
		sut.makeSoldOutConcertSeat(seat.id)

		//then
		val actual = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actual.reservedUntil).isNull()
	}

	@Test
	fun `좌석 매진 롤백 시, 해당 좌석의 만료 기한이 주어진 기한으로 변경된다`() {
		// given
		val testTime = LocalDateTime.of(2025, 1, 9, 22, 11, 37)
		val seat = ConcertSeatEntity(12, 15000, 3L, null)
		concertSeatJpaRepository.save(seat)

		// when
		sut.rollbackSoldOutedConcertSeat(seat.id, testTime)

		//then
		val actual = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actual.reservedUntil).isEqualTo(testTime)
	}
}