package kr.hhplus.be.server.domain.concert

import kr.hhplus.be.server.domain.KSelect.Companion.field
import org.assertj.core.api.Assertions.assertThat
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
		val concert1 = createConcert(1L, "콘서트1", "가수1", false)
		val concert2 = createConcert(2L, "콘서트2", "그룹1", false)
		val concert3 = createConcert(3L, "콘서트3", "가수2", false)
		val concerts = listOf(concert1, concert2, concert3)

		`when`(concertRepository.findAllConcert(false)).then { concerts }

		// when
		val actual = sut.getConcertInformation()

		//then
		verify(concertRepository).findAllConcert(false)

		assertThat(actual).hasSize(3)
			.anyMatch { it.concertId == 1L && it.title == "콘서트1" && it.provider == "가수1" }
			.anyMatch { it.concertId == 2L && it.title == "콘서트2" && it.provider == "그룹1" }
			.anyMatch { it.concertId == 3L && it.title == "콘서트3" && it.provider == "가수2" }
	}

	private fun createConcert(id: Long, title: String, provider: String, finished: Boolean): Concert =
		Instancio.of(Concert::class.java)
			.set(field(Concert::id), id)
			.set(field(Concert::title), title)
			.set(field(Concert::provider), provider)
			.set(field(Concert::finished), finished)
			.create()
}