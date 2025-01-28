package kr.hhplus.be.server.domain.reservation

import kr.hhplus.be.server.TestContainerCleaner
import kr.hhplus.be.server.common.exception.CustomException
import kr.hhplus.be.server.common.exception.ErrorCode
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.concert.entity.ConcertSeatEntity
import kr.hhplus.be.server.infrastructure.reservation.ReservationJpaRepository
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository
import kr.hhplus.be.server.infrastructure.user.entity.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class ReservationServiceConcurrencyTest(
	@Autowired private val testContainerCleaner: TestContainerCleaner,
	@Autowired private val sut: ReservationService,
	@Autowired private val userJpaRepository: UserJpaRepository,
	@Autowired private val reservationJpaRepository: ReservationJpaRepository,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository,
) {

	@BeforeEach
	fun setUp() {
		testContainerCleaner.clearAll()
	}

	@Test
	fun `콘서트 에약 시, 동일한 스케줄의 동일한 좌석에 대해 5개의 요청 동시에 들어와도 예약은 1개만 생겨야한다`() {
		// given
		val user1 = UserEntity("김항해", "유저토큰1", 1000)
		val user2 = UserEntity("나항해", "유저토큰2", 2000)
		val user3 = UserEntity("박항해", "유저토큰3", 3000)
		val user4 = UserEntity("이항해", "유저토큰4", 4000)
		val user5 = UserEntity("송항해", "유저토큰5", 5000)
		val users = listOf(user1, user2, user3, user4, user5)
		userJpaRepository.saveAll(users)

		val concert = ConcertEntity("항해콘", "나가수")
		concertJpaRepository.save(concert)

		val testTime = LocalDateTime.of(2025, 1, 17, 12, 59, 59)
		val schedule = ConcertScheduleEntity(50, testTime.plusHours(10), testTime.plusHours(13), concert.id)
		concertScheduleJpaRepository.save(schedule)

		val price = 900
		val seat = ConcertSeatEntity(11, price, schedule.id, testTime.minusMinutes(5))
		concertSeatJpaRepository.save(seat)

		val expiredAt = testTime.plusMinutes(5)
		val command1 = ReservationCommand.Create(price, user1.id, concert.id, schedule.id, seat.id, expiredAt)
		val command2 = ReservationCommand.Create(price, user2.id, concert.id, schedule.id, seat.id, expiredAt)
		val command3 = ReservationCommand.Create(price, user3.id, concert.id, schedule.id, seat.id, expiredAt)
		val command4 = ReservationCommand.Create(price, user4.id, concert.id, schedule.id, seat.id, expiredAt)
		val command5 = ReservationCommand.Create(price, user5.id, concert.id, schedule.id, seat.id, expiredAt)
		val commands = listOf(command1, command2, command3, command4, command5)

		val repeat = 5
		val countDownLatch = CountDownLatch(repeat)
		val executors = Executors.newFixedThreadPool(repeat)

		// when
		var successCount = 0
		val exceptions = mutableListOf<Exception>()
		for (i in 0 until repeat) {
			executors.execute {
				try {
					sut.reserve(commands[i]) { testTime }
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
			.allMatch { it is CustomException && it.errorCode == ErrorCode.ALREADY_RESERVED } // 뒤늦게 조회 시

		val actualReservations = reservationJpaRepository.findAll()
		assertThat(actualReservations).hasSize(1)
	}
}