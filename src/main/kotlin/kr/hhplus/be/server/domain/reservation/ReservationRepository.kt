package kr.hhplus.be.server.domain.reservation

interface ReservationRepository {

	fun findByScheduleAndSeatForUpdate(concertScheduleId: Long, concertSeatId: Long): Reservation?

	fun save(reservation: Reservation): Reservation
}