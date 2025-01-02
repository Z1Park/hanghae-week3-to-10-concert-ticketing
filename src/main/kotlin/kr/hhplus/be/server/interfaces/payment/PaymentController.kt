package kr.hhplus.be.server.interfaces.payment

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payments")
class PaymentController {

    @PostMapping("")
    fun pay(payRequest: PayRequest) {
    }
}