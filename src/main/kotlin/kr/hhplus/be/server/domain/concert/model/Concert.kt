package kr.hhplus.be.server.domain.concert.model

import kr.hhplus.be.server.domain.BaseDomain

class Concert(
	var title: String,

	var provider: String,

	var finished: Boolean = false,

	id: Long = 0L
) : BaseDomain(id) {
}