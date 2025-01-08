package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.domain.concert.Concert
import kr.hhplus.be.server.infrastructure.concert.ConcertJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertScheduleJpaRepository
import kr.hhplus.be.server.infrastructure.concert.ConcertSeatJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ConcertFacadeServiceIntegrationTest(
	@Autowired private val sut: ConcertFacadeService,
	@Autowired private val concertJpaRepository: ConcertJpaRepository,
	@Autowired private val concertScheduleJpaRepository: ConcertScheduleJpaRepository,
	@Autowired private val concertSeatJpaRepository: ConcertSeatJpaRepository
) {

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
}