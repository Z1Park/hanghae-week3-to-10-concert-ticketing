package kr.hhplus.be.server.application.concert

import kr.hhplus.be.server.common.component.ClockHolder
import kr.hhplus.be.server.domain.concert.ConcertCommand
import kr.hhplus.be.server.domain.concert.ConcertInfo
import kr.hhplus.be.server.domain.concert.ConcertService
import org.springframework.stereotype.Component

/**
 * MSA 환경에서 각 서비스의 외부 API 클라이언트는 Infrastructure 계층이 맞지만,
 * 현재 구조상 MSA 환경을 가정하고 사용하고 있고 Service/Domain을 호출하기 때문에 application 계층에 둔다.
 */
@Component
class ConcertApiClient(
	private val concertService: ConcertService,
	private val clockHolder: ClockHolder
) {

	fun concertApiPreoccupyConcert(command: ConcertCommand.Preoccupy, traceId: String): ConcertInfo.ReservedSeat =
		concertService.preoccupyConcertSeat(command, traceId, clockHolder)
}