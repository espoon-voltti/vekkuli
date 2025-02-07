package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.controllers.Utils
import fi.espoo.vekkuli.service.PaymentProcessErrorCode
import fi.espoo.vekkuli.service.PaytrailCallbackUrl
import java.net.URI

object ReservationPaymentConfig {
    const val SUCCESS_CALLBACK_PATH: String = "/api/citizen/public/paytrail/callback/success"
    const val CANCEL_CALLBACK_PATH: String = "/api/citizen/public/paytrail/callback/cancel"
    const val SUCCESS_REDIRECT_PATH: String = "/api/citizen/paytrail/redirect/success"
    const val CANCEL_REDIRECT_PATH: String = "/api/citizen/paytrail/redirect/cancel"

    fun confirmedFrontendUrl(reservationId: Int): URI {
        return URI.create(Utils.getServiceUrl("/kuntalainen/venepaikka/vahvistus/$reservationId"))
    }

    fun cancelledFrontendUrl(): URI {
        return URI.create(Utils.getServiceUrl("/kuntalainen/venepaikka/maksa?cancelled=true"))
    }

    fun errorFrontendUrl(
        reservationId: Int,
        errorType: PaymentProcessErrorCode
    ): URI {
        return URI.create(Utils.getServiceUrl("/kuntalainen/venepaikka/varausvirhe/$reservationId/$errorType"))
    }

    fun redirectUrls() =
        PaytrailCallbackUrl(
            Utils.getServiceUrl(SUCCESS_REDIRECT_PATH),
            Utils.getServiceUrl(CANCEL_REDIRECT_PATH)
        )

    fun callbackUrls() =
        if (Utils.isStagingOrProduction()) {
            PaytrailCallbackUrl(
                Utils.getServiceUrl(SUCCESS_CALLBACK_PATH),
                Utils.getServiceUrl(CANCEL_CALLBACK_PATH)
            )
        } else {
            null
        }
}
