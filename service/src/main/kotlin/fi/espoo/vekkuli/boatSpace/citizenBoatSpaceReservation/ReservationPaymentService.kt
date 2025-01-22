package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.boatSpace.boatSpaceSwitch.BoatSpaceSwitchService
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.BoatSpaceConfig.PAYTRAIL_PRODUCT_CODE
import fi.espoo.vekkuli.config.PaytrailEnv
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.CitizenWithDetails
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.PaymentType
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.discountedPriceInCents
import fi.espoo.vekkuli.utils.formatAsShortDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
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
        val logger = LoggerFactory.getLogger(ReservationPaymentService::class.java)
        // TODO use timeProvider?
        val reference = createReference("172200", paytrailEnv.merchantId, reservation.id, LocalDate.now())
        val amount = calculatePriceWithDiscount(reservation)
        // TODO: throw error?
        if (amount <= 0) {
            logger.error("Payment amount must be greater than zero, amount: $amount, reservationId: $reservation.id")
        }
        val description = "Venepaikka ${reservation.startDate.year} ${reservation.locationName} ${reservation.place}"
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
                        productCode = PAYTRAIL_PRODUCT_CODE,
                        paymentType = PaymentType.OnlinePayment
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
                            PAYTRAIL_PRODUCT_CODE,
                            description,
                            category
                        )
                    ),
                redirectUrls = ReservationPaymentConfig.redirectUrls(),
                callbackUrls = ReservationPaymentConfig.callbackUrls(),
            )
        )
    }

    fun calculatePriceWithDiscount(reservation: BoatSpaceReservationDetails): Int {
        val priceCents =
            if (switchService.isSwitchedReservation(reservation)) {
                switchService.getRevisedPrice(reservation)
            } else {
                reservation.priceCents
            }
        return discountedPriceInCents(priceCents, reservation.discountPercentage)
    }

    fun handlePaymentSuccess(params: Map<String, String>): PaymentHandleResult =
        when (val result = boatReservationService.handlePaymentResult(params, true)) {
            is PaymentProcessResult.Success ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.confirmedFrontendUrl(result.reservation.id)
                )

            is PaymentProcessResult.Failure ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
                )

            is PaymentProcessResult.HandledAlready ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
                )
        }

    fun handlePaymentCancel(params: Map<String, String>): PaymentHandleResult =
        when (val result = boatReservationService.handlePaymentResult(params, false)) {
            is PaymentProcessResult.Success ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
                )

            is PaymentProcessResult.Failure ->
                PaymentHandleResult(
                    processResult = result,
                    redirectUrl = ReservationPaymentConfig.cancelledFrontendUrl()
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
