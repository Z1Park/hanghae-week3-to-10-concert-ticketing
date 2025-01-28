package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ConcertServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ConcertService,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `좌석 선점 시, 동일한 좌석에 대한 5개의 요청 동시에 들어와도 1개만 성공해야한다`() {
		// given
		val concert = ConcertEntity("항해콘", "나가수")
		concertJpaRepository.save(concert)

		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)
		val schedule = ConcertScheduleEntity(50, testTime.plusHours(10), testTime.plusHours(13), concert.id)
		concertScheduleJpaRepository.save(schedule)

		val seat = ConcertSeatEntity(11, 900, schedule.id, testTime.minusMinutes(5))
		concertSeatJpaRepository.save(seat)

		val command = ConcertCommand.Reserve(concert.id, schedule.id, seat.id)

		val repeat = 5
		val countDownLatch = CountDownLatch(repeat)
		val executors = Executors.newFixedThreadPool(repeat)

		// when
		var successCount = 0
		val exceptions = mutableListOf<Exception>()
		for (i in 0 until repeat) {
			executors.execute {
				try {
					sut.preoccupyConcertSeat(command) { testTime }
					successCount++
				} catch (e: Exception) {
					exceptions.add(e)
				}
				countDownLatch.countDown()
			}
		}

		//then
		countDownLatch.await()

		assertThat(successCount).isEqualTo(1)
		assertThat(exceptions).hasSize(4)
			.allMatch { it is CustomException && it.errorCode == ErrorCode.ALREADY_RESERVED }

		val actual = concertSeatJpaRepository.findByIdOrNull(seat.id)!!
		assertThat(actual.reservedUntil).isEqualTo(testTime.plusMinutes(5))
	}
}