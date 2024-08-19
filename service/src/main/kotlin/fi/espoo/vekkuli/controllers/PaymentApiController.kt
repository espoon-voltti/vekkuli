package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.Paytrail.Companion.checkSignature
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.domain.handleReservationPaymentResult
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@Controller
@RequestMapping("/ext/payments")
class PaymentApiController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/paytrail/success")
    fun apiSuccess(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        if (!checkSignature(params)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))
        jdbi.inTransactionUnchecked {
            it.handleReservationPaymentResult(stamp, PaymentStatus.Success)
        }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/paytrail/cancel")
    fun apiCancel(
        @RequestParam params: Map<String, String>
    ): ResponseEntity<Void> {
        if (!checkSignature(params)) {
            return ResponseEntity(HttpStatus.UNAUTHORIZED)
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))
        jdbi.inTransactionUnchecked {
            it.handleReservationPaymentResult(stamp, PaymentStatus.Failed)
        }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
