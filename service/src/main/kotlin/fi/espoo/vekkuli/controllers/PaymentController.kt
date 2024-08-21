package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
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

    @Autowired
    lateinit var paytrail: Paytrail

    @GetMapping("/maksa")
    suspend fun payment(
        @RequestParam id: Int,
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

        val payment =
            jdbi.inTransactionUnchecked {
                it.insertPayment(
                    CreatePaymentParams(
                        citizenId = citizen.id,
                        reference = reference,
                        totalCents = amount,
                        vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                        productCode = productCode
                    )
                )
            }
        jdbi.inTransactionUnchecked { it.updateReservationWithPayment(id, payment.id) }

        val response =
            paytrail.createPayment(
                PaytrailPaymentParams(
                    stamp = payment.id.toString(),
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
                    items = listOf(PaytrailPurchaseItem(amount, 1, BOAT_RESERVATION_ALV_PERCENTAGE, productCode))
                )
            )
        model.addAttribute("providers", response.providers)
        return "boat-space-reservation-payment"
    }

    @GetMapping("/onnistunut")
    fun success(
        @RequestParam params: Map<String, String>
    ): String {
        val result =
            paytrail.handlePaymentResult(params, true)

        when (result) {
            is PaymentProcessResult.Success -> return redirectUrl("/kuntalainen/venepaikka/varaus/${result.reservation.id}/vahvistus")
            is PaymentProcessResult.Failure -> return redirectUrl("/")
            is PaymentProcessResult.HandledAlready -> return redirectUrl("/")
        }
    }

    @GetMapping("/peruuntunut")
    fun cancel(
        @RequestParam params: Map<String, String>
    ): String {
        paytrail.handlePaymentResult(params, false)
        return "boat-space-reservation-payment-cancel"
    }
}
