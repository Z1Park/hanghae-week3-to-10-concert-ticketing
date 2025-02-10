package kr.hhplus.be.server.infrastructure.reservation

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import kr.hhplus.be.server.infrastructure.reservation.entity.QReservationEntity.reservationEntity
import kr.hhplus.be.server.infrastructure.reservation.entity.ReservationEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ReservationCustomRepository(
	private val entityManager: EntityManager
) {
	private val queryFactory = JPAQueryFactory(entityManager)

	fun findConcertIdByCount(
		start: LocalDateTime,
		end: LocalDateTime,
		limit: Long
	): List<ReservationEntity> {
		return queryFactory
			.select(reservationEntity)
			.from(reservationEntity)
			.where(reservationEntity.createdAt.between(start, end))
			.groupBy(reservationEntity.concertId)
			.orderBy(reservationEntity.concertId.count().desc())
			.limit(limit)
			.fetch();
	}
}