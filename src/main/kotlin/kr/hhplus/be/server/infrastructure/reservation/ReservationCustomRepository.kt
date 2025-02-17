package kr.hhplus.be.server.infrastructure.reservation

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import kr.hhplus.be.server.infrastructure.reservation.entity.QReservationEntity.reservationEntity
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
	): List<ConcertCountProjection> {
		return queryFactory
			.select(
				Projections.constructor(
					ConcertCountProjection::class.java,
					reservationEntity.concertId,
					reservationEntity.concertId.count()
				)
			)
			.from(reservationEntity)
			.where(
				reservationEntity.createdAt.between(start, end)
					.and(reservationEntity.expiredAt.isNull)
			)
			.groupBy(reservationEntity.concertId)
			.orderBy(reservationEntity.concertId.count().desc())
			.limit(limit)
			.fetch();
	}
}