package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

sealed class PaymentProcessResult {
    data class Success(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()

    object Failure : PaymentProcessResult()

    data class HandledAlready(
        val reservation: BoatSpaceReservationDetails
    ) : PaymentProcessResult()
}

@Service
class BoatReservationService {
    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var emailService: TemplateEmailService

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var paytrail: Paytrail

    fun handlePaymentResult(
        params: Map<String, String>,
        success: Boolean
    ): PaymentProcessResult {
        if (!paytrail.checkSignature(params)) {
            return PaymentProcessResult.Failure
        }
        val stamp = UUID.fromString(params.get("checkout-stamp"))

        val payment = jdbi.inTransactionUnchecked { it.getPayment(stamp) }
        if (payment == null) return PaymentProcessResult.Failure

        val reservation = jdbi.inTransactionUnchecked { it.getBoatSpaceReservationWithPaymentId(stamp) }
        if (reservation == null) return PaymentProcessResult.Failure

        if (payment.status != PaymentStatus.Created) return PaymentProcessResult.HandledAlready(reservation)

        jdbi.inTransactionUnchecked {
            it.handleReservationPaymentResult(stamp, success)
        }

        if (!success) {
            return PaymentProcessResult.Success(reservation)
        }

        emailService.sendEmail(
            "reservationSuccess",
            reservation.email,
            messageUtil.getMessage("boatSpaceReservation.title.confirmation"),
            mapOf(
                "name" to " ${reservation.locationName} ${reservation.place}",
                "width" to reservation.boatSpaceWidthInM,
                "length" to reservation.boatSpaceLengthInM,
                "amenity" to messageUtil.getMessage("boatSpaces.amenityOption.${reservation.amenity}"),
                "endDate" to reservation.endDate
            )
        )

        return PaymentProcessResult.Success(reservation)
    }
}
