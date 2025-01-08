package kr.hhplus.be.server.interfaces.reservation

import kr.hhplus.be.server.exception.ConflictException
import kr.hhplus.be.server.exception.ForbiddenException
import kr.hhplus.be.server.exception.UnauthorizedException
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.*
import java.time.ZonedDateTime

@RestController
@RequestMapping("/reservations")
class ReservationController {

    @PostMapping("")
    fun reserveConcertSeat(
        @CookieValue("user-access-token") userAccessToken: String?,
        @CookieValue("concert-access-token") concertAccessToken: String?,
        @RequestBody reserveConcertRequest: ReserveConcertRequest
    ): ReserveConcertResponse {
        require(reserveConcertRequest.concertId != 0L) { throw BadRequestException() }
        require(!userAccessToken.isNullOrBlank()) { throw UnauthorizedException() }
        require(!concertAccessToken.isNullOrBlank()) { throw ForbiddenException() }
        require(reserveConcertRequest.concertId != -1L) { throw ConflictException() }

        return ReserveConcertResponse(438L, ZonedDateTime.now().plusDays(3))
    }
}