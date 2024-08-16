package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.Paytrail
import fi.espoo.vekkuli.config.PaytrailCustomer
import fi.espoo.vekkuli.config.PaytrailPaymentParams
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.insertPayment
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

enum class PaymentType {
    BoatSpaceReservation
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
        // TODO get correct reference
        val reference = Math.random().toString()
        // TODO get correct amount
        val amount = 14000
        // TODO get correct product code
        val productCode = "Venepaikka A 100"

        val stamp = UUID.randomUUID().toString()

        val payment =
            jdbi.inTransactionUnchecked {
                it.insertPayment(
                    CreatePaymentParams(
                        citizenId = citizen.id,
                        reference = reference,
                        total_cents = amount,
                        vat_percentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                        // TODO: get product code from somewhere
                        product_code = productCode
                    )
                )
            }

        val response =
            Paytrail.createPayment(
                PaytrailPaymentParams(
                    stamp = stamp,
                    reference = reference,
                    amount = amount,
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

        println(payment)
        model.addAttribute("providers", response.providers)
        return "boat-space-reservation-payment"
    }
}
