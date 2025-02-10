package kr.hhplus.be.server.infrastructure.user

import kr.hhplus.be.server.infrastructure.user.entity.PointHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PointHistoryJpaRepository : JpaRepository<PointHistoryEntity, Long> {
}