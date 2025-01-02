package kr.hhplus.be.server.interfaces.queue

import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tokens")
class QueueController {

    @PostMapping("")
    fun publishQueueToken(): ResponseEntity<Unit> {
        return ResponseEntity.ok()
            .header(
                SET_COOKIE,
                ResponseCookie.from("concert-access-token", "DH8FF4NKJD082").build().toString()
            )
            .body(Unit)
    }

    @GetMapping("")
    fun getWaitingInformation(): WaitingInformationResponse {
        return WaitingInformationResponse(381297L, 25)
    }
}