package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.domain.user.PointHistory
import org.springframework.data.jpa.repository.JpaRepository

interface PointHistoryJpaRepository : JpaRepository<PointHistory, Long> {
}