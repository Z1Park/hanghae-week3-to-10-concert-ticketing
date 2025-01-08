package kr.hhplus.be.server.domain

import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	var id: Long = 0L,

	@CreatedDate
	var createdAt: LocalDateTime = LocalDateTime.now(),

	@LastModifiedDate
	var updatedAt: LocalDateTime = LocalDateTime.now()
) {

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false

		val oEffectiveClass =
			if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
		val thisEffectiveClass =
			if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass

		if (thisEffectiveClass != oEffectiveClass) return false

		other as BaseEntity
		return id == other.id
	}

	final override fun hashCode(): Int =
		if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}