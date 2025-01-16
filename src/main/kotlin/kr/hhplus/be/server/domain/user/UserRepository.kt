package kr.hhplus.be.server.domain.user

interface UserRepository {

	fun findById(userId: Long): User?

	fun findByUuid(uuid: String): User?

	fun findByUuidForUpdate(uuid: String): User?

	fun findPointHistoryById(pointHistoryId: Long): PointHistory?

	fun save(user: User): User

	fun save(pointHistory: PointHistory): PointHistory

	fun delete(pointHistory: PointHistory)
}