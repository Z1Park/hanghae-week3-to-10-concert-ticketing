package kr.hhplus.be.server.domain.concert

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
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
) : BaseEntity() {
}