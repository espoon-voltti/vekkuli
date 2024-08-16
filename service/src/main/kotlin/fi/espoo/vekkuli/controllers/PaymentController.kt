package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.Paytrail
import fi.espoo.vekkuli.config.PaytrailCustomer
import fi.espoo.vekkuli.config.PaytrailPaymentParams
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

enum class PaymentType {
    BOAT_SPACE_RESERVATION
}

@Controller
@RequestMapping("/kuntalainen/maksut")
class PaymentController {
    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/maksa")
    suspend fun payment(
        @RequestParam id: String,
        @RequestParam type: PaymentType,
        model: Model,
        request: HttpServletRequest
    ): String {
        val citizen = getCitizen(request, jdbi) ?: return redirectUrl("/")

        val response =
            Paytrail.createPayment(
                PaytrailPaymentParams(
                    stamp = Math.random().toString(),
                    reference = Math.random().toString(),
                    amount = 1525,
                    language = "FI",
                    customer =
                        PaytrailCustomer(
                            email = citizen.email,
                            firstName = citizen.firstName,
                            lastName = citizen.lastName,
                            phone = citizen.phone
                        ),
                )
            )

        model.addAttribute("providers", response.providers)
        return "boat-space-reservation-payment"
    }
}
