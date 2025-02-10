package kr.hhplus.be.server.domain

import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseDomain(
	var id: Long,

	var createdAt: LocalDateTime = LocalDateTime.now(),

	var updatedAt: LocalDateTime = LocalDateTime.now()
) {

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false

		if (other !is BaseDomain) return false
		return id == other.id
	}

	final override fun hashCode(): Int =
		javaClass.hashCode()
}