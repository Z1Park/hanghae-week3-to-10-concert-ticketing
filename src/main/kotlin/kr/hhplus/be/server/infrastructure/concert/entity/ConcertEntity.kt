package kr.hhplus.be.server.infrastructure.concert.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.domain.concert.model.Concert
import kr.hhplus.be.server.infrastructure.BaseEntity

@Entity
@Table(name = "concert")
class ConcertEntity(
	@Column(nullable = false)
	var title: String,

	@Column(nullable = false)
	var provider: String,

	@Column(nullable = false)
	var finished: Boolean = false,

	id: Long = 0L
) : BaseEntity(id) {

	constructor(concert: Concert) : this(
		id = concert.id,
		title = concert.title,
		provider = concert.provider,
		finished = concert.finished
	)

	fun toDomain(): Concert = Concert(
		id = id,
		title = title,
		provider = provider,
		finished = finished
	)
}