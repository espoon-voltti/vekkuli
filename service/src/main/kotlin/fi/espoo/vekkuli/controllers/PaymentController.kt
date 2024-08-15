package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.Paytrail
import fi.espoo.vekkuli.config.PaytrailCustomer
import fi.espoo.vekkuli.config.PaytrailPaymentParams
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/maksut")
class PaymentController {
    @GetMapping("/maksa")
    suspend fun payment(model: Model): String {
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

        model.addAttribute("providers", response.providers)
        return "boat-space-reservation-payment"
    }
}
