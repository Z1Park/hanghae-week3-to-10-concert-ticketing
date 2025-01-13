package kr.hhplus.be.server.domain.exception

class ReservationExpiredException : RuntimeException("이미 만료된 예약입니다.") {
}