package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.boatSpace.reservationForm.getReservationTimeInSeconds
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.BoatSpaceConfig.paytrailDescription
import fi.espoo.vekkuli.config.BoatSpaceConfig.paytrailProductCode
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.config.PaytrailEnv
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrlThymeleaf
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.formatAsShortDate
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mu.KotlinLogging
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
): String = "$balanceAccount$merchantId${formatAsShortDate(now)}$reservationId"

@Controller
@RequestMapping("/kuntalainen/maksut")
class PaymentController(
    private val reservationService: BoatReservationService,
    private val paytrail: PaytrailInterface,
    private val messageUtil: MessageUtil,
    private val reserverService: ReserverService,
    private val paytrailEnv: PaytrailEnv,
    private val timeProvider: TimeProvider,
    private val boatReservationService: BoatReservationService,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/maksa")
    suspend fun payment(
        @RequestParam id: Int,
        @RequestParam type: PaymentType,
        @RequestParam cancelled: Boolean? = false,
        model: Model,
        request: HttpServletRequest,
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "PAYMENT_VIEW",
                mapOf(
                    "targetId" to id.toString(),
                    "type" to type.toString(),
                    "cancelled" to cancelled.toString()
                )
            )
        }
        val locale = LocaleContextHolder.getLocale()
        val citizen = getCitizen(request, reserverService) ?: return redirectUrlThymeleaf("/")
        val reservation = reservationService.getBoatSpaceReservation(id) ?: return redirectUrlThymeleaf("/")

        val reference = createReference("172200", paytrailEnv.merchantId, reservation.id, LocalDate.now())
        val amount = reservation.priceCents
        // TODO must this be configurable?

        val category = "MYY255"

        val payment =
            withContext(Dispatchers.IO) {
                reservationService.addPaymentToReservation(
                    id,
                    CreatePaymentParams(
                        reserverId = citizen.id,
                        reference = reference,
                        totalCents = amount,
                        vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                        productCode = paytrailProductCode(reservation.type),
                        paymentType = PaymentType.OnlinePayment
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
                            email = citizen.email.take(200),
                            firstName = citizen.firstName.take(50),
                            lastName = citizen.lastName.take(50),
                            phone = citizen.phone
                        ),
                    items =
                        listOf(
                            PaytrailPurchaseItem(
                                amount,
                                1,
                                BOAT_RESERVATION_ALV_PERCENTAGE,
                                paytrailProductCode(reservation.type),
                                paytrailDescription(reservation),
                                category
                            )
                        )
                )
            )
        val errorMessage = if (cancelled == true) messageUtil.getMessage("payment.cancelled", locale = locale) else null
        model.addAttribute("providers", response.providers)
        model.addAttribute("error", errorMessage)
        model.addAttribute(
            "reservationTimeInSeconds",
            getReservationTimeInSeconds(
                reservation.created,
                timeProvider.getCurrentDateTime()
            )
        )
        model.addAttribute("reservationId", reservation.id)

        return "boat-space-reservation-payment"
    }

    @GetMapping("/onnistunut")
    fun success(
        @RequestParam params: Map<String, String>,
        request: HttpServletRequest
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "PAYMENT_SUCCESS_VIEW", mapOf("params" to params.toString()))
        }
        val result =
            reservationService.handlePaymentResult(params, true)

        when (result) {
            is PaymentProcessResult.Success -> {
                // End the original reservation
                if (result.reservation.originalReservationId != null) {
                    boatReservationService.markReservationEnded(result.reservation.originalReservationId)
                }
                return redirectUrlThymeleaf(
                    "/kuntalainen/venepaikka/varaus/${result.reservation.id}/vahvistus"
                )
            }
            is PaymentProcessResult.Failure -> return redirectUrlThymeleaf("/")
            is PaymentProcessResult.HandledAlready -> return redirectUrlThymeleaf("/")
        }
    }

    @GetMapping("/peruuntunut")
    fun cancel(
        @RequestParam params: Map<String, String>,
        request: HttpServletRequest
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "PAYMENT_CANCEL_VIEW", mapOf("params" to params.toString()))
        }

        return when (val result = reservationService.handlePaymentResult(params, false)) {
            is PaymentProcessResult.Failure -> return redirectUrlThymeleaf("/")
            is PaymentProcessResult.Success -> return redirectUrlThymeleaf(
                "/kuntalainen/maksut/maksa?id=${result.reservation.id}&type=BoatSpaceReservation&cancelled=true"
            )
            is PaymentProcessResult.HandledAlready ->
                redirectUrlThymeleaf(
                    "/kuntalainen/maksut/maksa?id=${result.reservation.id}&type=BoatSpaceReservation&cancelled=true"
                )
        }
    }
}
