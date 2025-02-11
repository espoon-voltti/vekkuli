package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.BoatSpaceConfig.paytrailDescription
import fi.espoo.vekkuli.config.BoatSpaceConfig.paytrailProductCode
import fi.espoo.vekkuli.config.PaytrailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.discountedPriceInCents
import fi.espoo.vekkuli.utils.formatAsShortDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.net.URI
import java.time.LocalDate

@Service
class ReservationPaymentService(
    private val boatReservationService: BoatReservationService,
    private val paytrail: PaytrailInterface,
    private val paytrailEnv: PaytrailEnv,
    private val switchService: BoatSpaceSwitchService,
) {
    suspend fun createPaymentForBoatSpaceReservation(
        citizen: CitizenWithDetails,
        reservation: BoatSpaceReservationDetails
    ): PaytrailPaymentResponse {
        // TODO use timeProvider?
        val reference = createReference("172200", paytrailEnv.merchantId, reservation.id, LocalDate.now())
        val price = calculatePrice(reservation)
        val amount = discountedPriceInCents(price, reservation.discountPercentage)
        val priceInfo = getPriceInfo(reservation.creationType, price, reservation.discountPercentage)
        if (amount <= 0) {
            throw IllegalArgumentException("Payment amount must be greater than zero, reservationId: $reservation.id")
        }
        val category = "MYY255"
        val payment =
            withContext(Dispatchers.IO) {
                boatReservationService.addPaymentToReservation(
                    reservation.id,
                    CreatePaymentParams(
                        reserverId = citizen.id,
                        reference = reference,
                        totalCents = amount,
                        vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                        productCode = paytrailProductCode(reservation.type),
                        paymentType = PaymentType.OnlinePayment,
                        priceInfo = priceInfo
                    )
                )
            }

        return paytrail.createPayment(
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
                    ),
                redirectUrls = ReservationPaymentConfig.redirectUrls(),
                callbackUrls = ReservationPaymentConfig.callbackUrls(),
            )
        )
    }

    fun getPriceInfo(
        reservationCreationType: CreationType,
        revisedPrise: Int,
        reserverDiscountPercentage: Int
    ): String {
        var text = ""
        if (reservationCreationType == CreationType.Switch) {
            text = "Paikan vaihto. "
            when {
                revisedPrise > 0 -> {
                    text += "Maksettu vain erotus. "
                    if (reserverDiscountPercentage > 0) {
                        text += "Hinnassa huomioitu $reserverDiscountPercentage% alennus. "
                    }
                }
                revisedPrise < 0 -> text += "Ei suoritusta, uusi paikka edullisempi. "
                else -> text += "Ei suoritusta, paikoilla sama hinta. "
            }
        } else if (reserverDiscountPercentage > 0) {
            text += "Hinnassa huomioitu $reserverDiscountPercentage% alennus."
        }

        return text.trim()
    }

    fun calculatePrice(reservation: BoatSpaceReservationDetails): Int =
        if (switchService.isSwitchedReservation(reservation)) {
            switchService.getRevisedPrice(reservation.originalReservationId, reservation.priceCents)
        } else {
            reservation.priceCents
        }

    fun handlePaymentSuccess(params: Map<String, String>): PaymentHandleResult =
        createPaymentHandleResult(boatReservationService.handlePaytrailPaymentResult(params, true))

    fun handlePaymentCancel(params: Map<String, String>): PaymentHandleResult =
        createPaymentHandleResult(boatReservationService.handlePaytrailPaymentResult(params, false))

    private fun createPaymentHandleResult(result: PaymentProcessResult): PaymentHandleResult =
        when (result) {
            is PaymentProcessResult.Paid ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.confirmedFrontendUrl(result.reservation.id)
                )
            is PaymentProcessResult.Cancelled ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
                )
            is PaymentProcessResult.Failure ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl =
                        when {
                            result.reservation != null -> ReservationPaymentConfig.errorFrontendUrl(result.reservation.id, result.errorCode)
                            else -> ReservationPaymentConfig.cancelledFrontendUrl()
                        }
                )
            is PaymentProcessResult.HandledAlready ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
                )
        }

    private fun createReference(
        balanceAccount: String,
        merchantId: String,
        reservationId: Int,
        now: LocalDate
    ): String = "$balanceAccount$merchantId${formatAsShortDate(now)}$reservationId"
}

data class PaymentHandleResult(
    val processResult: PaymentProcessResult,
    val redirectUrl: URI,
)
