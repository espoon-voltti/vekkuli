package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.service.Paytrail
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/ext/payments")
class PaymentApiController {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var paytrail: Paytrail

    @GetMapping("/paytrail/success")
    fun apiSuccess(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        paytrail.handlePaymentResult(params, true)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/paytrail/cancel")
    fun apiCancel(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        paytrail.handlePaymentResult(params, false)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
