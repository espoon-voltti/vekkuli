package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.PaytrailEnv
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.dateToShortString
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

fun createReference(
    balanceAccount: String,
    merchantId: String,
    reservationId: Int,
    now: LocalDate
): String = "$balanceAccount$merchantId${dateToShortString(now)}$reservationId"

@Controller
@RequestMapping("/kuntalainen/maksut")
class PaymentController(
    private val reservationService: BoatReservationService,
    private val paytrail: PaytrailInterface,
    private val messageUtil: MessageUtil,
    private val citizenService: CitizenService,
    private val paytrailEnv: PaytrailEnv,
    private val timeProvider: TimeProvider
) {
    @GetMapping("/maksa")
    suspend fun payment(
        @RequestParam id: Int,
        @RequestParam type: PaymentType,
        @RequestParam cancelled: Boolean? = false,
        model: Model,
        request: HttpServletRequest,
    ): String {
        val locale = LocaleContextHolder.getLocale()
        val citizen = getCitizen(request, citizenService) ?: return redirectUrl("/")
        val reservation = reservationService.getBoatSpaceReservation(id) ?: return redirectUrl("/")

        val reference = createReference("172200", paytrailEnv.merchantId, reservation.id, LocalDate.now())
        val amount = reservation.priceCents
        val description = "Venepaikka ${reservation.startDate.year} ${reservation.locationName} ${reservation.place}"
        // TODO must this be configurable?
        val productCode = "329700-1230329-T1270-0-0-0-0-0-0-0-0-0-100"

        val category = "MYY255"

        val payment =
            withContext(Dispatchers.IO) {
                reservationService.addPaymentToReservation(
                    id,
                    CreatePaymentParams(
                        citizenId = citizen.id,
                        reference = reference,
                        totalCents = amount,
                        vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                        productCode = productCode
                    )
                )
            }

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
                    items = listOf(PaytrailPurchaseItem(amount, 1, BOAT_RESERVATION_ALV_PERCENTAGE, productCode, description, category))
                )
            )
        val errorMessage = if (cancelled == true) messageUtil.getMessage("payment.cancelled", locale = locale) else null
        model.addAttribute("providers", response.providers)
        model.addAttribute("error", errorMessage)
        model.addAttribute("reservationTimeInSeconds", getReservationTimeInSeconds(reservation.created, timeProvider.getCurrentDate()))
        model.addAttribute("reservationId", reservation.id)

        return "boat-space-reservation-payment"
    }

    @GetMapping("/onnistunut")
    fun success(
        @RequestParam params: Map<String, String>,
    ): String {
        val result =
            reservationService.handlePaymentResult(params, true)

        when (result) {
            is PaymentProcessResult.Success -> return redirectUrl("/kuntalainen/venepaikka/varaus/${result.reservation.id}/vahvistus")
            is PaymentProcessResult.Failure -> return redirectUrl("/")
            is PaymentProcessResult.HandledAlready -> return redirectUrl("/")
        }
    }

    @GetMapping("/peruuntunut")
    fun cancel(
        @RequestParam params: Map<String, String>,
    ): String {
        return when (val result = reservationService.handlePaymentResult(params, false)) {
            is PaymentProcessResult.Failure -> return redirectUrl("/")
            is PaymentProcessResult.Success -> return redirectUrl(
                "/kuntalainen/maksut/maksa?id=${result.reservation.id}&type=BoatSpaceReservation&cancelled=true"
            )
            is PaymentProcessResult.HandledAlready ->
                redirectUrl(
                    "/kuntalainen/maksut/maksa?id=${result.reservation.id}&type=BoatSpaceReservation&cancelled=true"
                )
        }
    }
}
