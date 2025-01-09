package kr.hhplus.be.server.domain.reservation

interface ReservationRepository {

	fun findByUserIdAndReservationId(userId: Long, reservationId: Long): Reservation?

	fun findByScheduleAndSeatForUpdate(concertScheduleId: Long, concertSeatId: Long): Reservation?

	fun save(reservation: Reservation): Reservation
}