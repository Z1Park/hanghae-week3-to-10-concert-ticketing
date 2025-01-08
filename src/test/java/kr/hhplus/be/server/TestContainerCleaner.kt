package kr.hhplus.be.server

import jakarta.persistence.EntityManager
import jakarta.persistence.Table
import jakarta.transaction.Transactional
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class TestContainerCleaner(
	private val jdbcTemplate: JdbcTemplate,
	private val entityManager: EntityManager
) {

	@Transactional
	fun clearAll() {
		val tables = entityManager.metamodel.entities.map { entity ->
			val tableAnnotation = entity.javaType.getAnnotation(Table::class.java)
			tableAnnotation?.name ?: entity.name
		}

		tables.forEach { jdbcTemplate.execute("TRUNCATE TABLE $it") }
	}
}