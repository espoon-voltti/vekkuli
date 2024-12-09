package fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation

import fi.espoo.vekkuli.controllers.Utils
import fi.espoo.vekkuli.service.PaytrailCallbackUrl
import java.net.URI

object ReservationPaymentConfig {
    const val successCallbackPath: String = "/api/citizen/public/paytrail/callback/success"
    const val cancelCallbackPath: String = "/api/citizen/public/paytrail/callback/cancel"
    const val successRedirectPath: String = "/api/citizen/paytrail/redirect/success"
    const val cancelRedirectPath: String = "/api/citizen/paytrail/redirect/cancel"

    fun confirmedFrontendUrl(reservationId: Int): URI {
        return URI.create(Utils.getServiceUrl("/kuntalainen/venepaikka/vahvistus/$reservationId"))
    }

    fun cancelledFrontendUrl(): URI {
        return URI.create(Utils.getServiceUrl("/kuntalainen/venepaikka/maksa?cancelled=true"))
    }

    fun redirectUrls() =
        PaytrailCallbackUrl(
            Utils.getServiceUrl(successRedirectPath),
            Utils.getServiceUrl(cancelRedirectPath)
        )

    fun callbackUrls() =
        if (Utils.isStagingOrProduction()) {
            PaytrailCallbackUrl(
                Utils.getServiceUrl(successCallbackPath),
                Utils.getServiceUrl(cancelCallbackPath)
            )
        } else {
            null
        }
}