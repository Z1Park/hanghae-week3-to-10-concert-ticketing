package kr.hhplus.be.server.interfaces.user

import org.springframework.http.HttpHeaders.SET_COOKIE
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController {

    @PostMapping("/{userId}")
    fun publishUserToken(@PathVariable userId: Long) : ResponseEntity<Unit> {
        return ResponseEntity.ok()
            .header(
                SET_COOKIE,
                ResponseCookie.from("user-access-token", "EI9137BFKJD98").build().toString()
            )
            .body(Unit)
    }

    @GetMapping("/points")
    fun getRemainPoint(): RemainPointResponse {
        return RemainPointResponse(20000)
    }

    @PostMapping("/points")
    fun chargePoint(chargePointRequest: ChargePointRequest) {
    }
}