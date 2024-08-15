package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.Paytrail
import fi.espoo.vekkuli.config.PaytrailCustomer
import fi.espoo.vekkuli.config.PaytrailPaymentParams
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/maksut")
class PaymentController {
    @GetMapping("/maksa")
    suspend fun payment(): String {
        val response =
            Paytrail.createPayment(
                PaytrailPaymentParams(
                    stamp = Math.random().toString(),
                    reference = Math.random().toString(),
                    amount = 1525,
                    language = "FI",
                    customer = PaytrailCustomer(email = "test.customer@example.com", firstName = null, lastName = null, phone = null),
                )
            )
        println(response)
        return "payment"
    }
}
