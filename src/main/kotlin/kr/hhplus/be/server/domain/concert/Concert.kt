package kr.hhplus.be.server.domain.concert

import jakarta.persistence.*
import kr.hhplus.be.server.domain.BaseEntity

@Entity
@Table(name = "concert")
class Concert(
	@Column(nullable = false)
	var title: String,

	@Column(nullable = false)
	var provider: String,

	@Column(nullable = false)
	var finished: Boolean = false,

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "concertId")
	val concertSchedules: List<ConcertSchedule> = mutableListOf()
) : BaseEntity() {
}