package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.*
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.Paytrail.Companion.checkSignature
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.*
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
    lateinit var messageUtil: MessageUtil

    @GetMapping("/maksa")
    suspend fun payment(
        @RequestParam id: Int,
        @RequestParam type: PaymentType,
        @RequestParam cancelled: Boolean? = false,
        model: Model,
        request: HttpServletRequest,
    ): String {
        val citizen = getCitizen(request, jdbi) ?: return redirectUrl("/")
        // TODO get correct reference
        val reference = Math.random().toString()
        // TODO get correct amount
        val amount = 14000
        // TODO get correct product code
        val productCode = "Venepaikka A 100"

        val (payment, reservation) =
            jdbi.inTransactionUnchecked {
                val payment =
                    it.insertPayment(
                        CreatePaymentParams(
                            citizenId = citizen.id,
                            reference = reference,
                            totalCents = amount,
                            vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                            productCode = productCode
                        )
                    )
                val reservation = it.updateReservationWithPayment(id, payment.id)
                return@inTransactionUnchecked payment to reservation
            }

        val response =
            Paytrail.createPayment(
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
        val errorMessage = if (cancelled == true) messageUtil.getMessage("payment.cancelled") else null
        model.addAttribute("providers", response.providers)
        model.addAttribute("error", errorMessage)
        model.addAttribute("reservationTimeInSeconds", getReservationTimeInSeconds(reservation.created))
        return "boat-space-reservation-payment"
    }

    @GetMapping("/onnistunut")
    fun success(
        @RequestParam params: Map<String, String>,
    ): String {
        if (!checkSignature(params)) {
            return redirectUrl("/")
        }

        val stamp = UUID.fromString(params.get("checkout-stamp"))
        val reservationId =
            jdbi.inTransactionUnchecked {
                it.handleReservationPaymentResult(stamp, PaymentStatus.Success)
            }
        if (reservationId == null) return redirectUrl("/")
        return redirectUrl("/kuntalainen/venepaikka/varaus/$reservationId/vahvistus")
    }

    @GetMapping("/peruuntunut")
    fun cancel(
        @RequestParam params: Map<String, String>,
    ): String {
        if (!checkSignature(params)) {
            return redirectUrl("/")
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))

        val reservationId =
            jdbi.inTransactionUnchecked {
                it.handleReservationPaymentResult(stamp, PaymentStatus.Failed)
            }

        return redirectUrl("/kuntalainen/maksut/maksa?id=$reservationId&type=BoatSpaceReservation&cancelled=true")
    }
}
