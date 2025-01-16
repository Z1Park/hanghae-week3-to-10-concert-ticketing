package kr.hhplus.be.server.domain.reservation

interface ReservationRepository {

	fun findById(reservationId: Long): Reservation?

	fun findByScheduleAndSeatForUpdate(concertScheduleId: Long, concertSeatId: Long): Reservation?

	fun save(reservation: Reservation): Reservation

	fun delete(seatReservation: Reservation)
}