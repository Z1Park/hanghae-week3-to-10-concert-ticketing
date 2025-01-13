package kr.hhplus.be.server.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class AlreadyReservedException() : RuntimeException("이미 선택된 좌석입니다.") {
}