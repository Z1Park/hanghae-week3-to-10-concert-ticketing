package kr.hhplus.be.server.interfaces.reservation

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("/reservations")
class ReservationController {

    @PostMapping("")
    fun reserveConcertSeat(
        @RequestBody reserveConcertRequest: ReserveConcertRequest
    ): ReserveConcertResponse {
        return ReserveConcertResponse(438L, ZonedDateTime.now().plusDays(3))
    }
}