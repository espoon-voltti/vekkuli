package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.stereotype.Component

@Component
class ReservationTerminationReason : BaseView() {
    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
    ): String {
        if (reservation.status != ReservationStatus.Cancelled || reservation.terminationReason == null) {
            return ""
        }
        // language=HTML
        return """
            <div class="columns is-error-container p-none mb-s">
                 <div class="column" ${addTestId("reservation-card-termination-reason")}>
                    ${getTerminationReason(reservation)}
                </div>
                <div class="column" ${addTestId("reservation-card-termination-explanation")}>
                    ${getTerminationExplanation(reservation)}
                </div>
            </div>
            """.trimIndent()
    }

    private fun getTerminationReason(reservation: BoatSpaceReservationDetails): String {
        if (reservation.terminationReason == null) {
            return ""
        }
        val label = t("boatSpaceTermination.fields.reason")
        val translatedReason =
            t(
                "boatSpaceReservation.terminateReason." +
                    reservation.terminationReason.name.replaceFirstChar { char ->
                        char.lowercase()
                    }
            )
        return """
            <label class="label is-inline mr-s">$label:</label><span>$translatedReason</span>  
            """.trimIndent()
    }

    private fun getTerminationExplanation(reservation: BoatSpaceReservationDetails): String {
        val label = t("boatSpaceTermination.fields.explanation")

        val comment =
            if (reservation.terminationComment == null ||
                reservation.terminationComment
                    .trim()
                    .isEmpty()
            ) {
                " - "
            } else {
                reservation.terminationComment.trim()
            }

        return """
            <label class="label is-inline mr-s">$label:</label><span>$comment</span>  
            """.trimIndent()
    }
}
