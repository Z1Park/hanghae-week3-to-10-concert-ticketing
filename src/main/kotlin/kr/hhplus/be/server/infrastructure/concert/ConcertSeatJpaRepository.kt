package kr.hhplus.be.server.infrastructure.concert

import kr.hhplus.be.server.domain.concert.ConcertSeat
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertSeatJpaRepository : JpaRepository<ConcertSeat, Long> {
}