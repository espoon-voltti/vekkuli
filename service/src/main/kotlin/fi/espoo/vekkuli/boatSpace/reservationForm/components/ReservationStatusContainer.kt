package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

// language=HTML
@Component
class ReservationStatusContainer(
    private val formComponents: FormComponents,
) : BaseView() {
    val notPaid = "NotPaid"

    val statusOptions = { previousStatus: ReservationStatus ->
        when (previousStatus) {
            ReservationStatus.Info, ReservationStatus.Payment ->
                listOf(
                    notPaid,
                    ReservationStatus.Invoiced.toString(),
                    ReservationStatus.Confirmed.toString(),
                )
            ReservationStatus.Invoiced ->
                listOf(
                    ReservationStatus.Invoiced.toString(),
                    ReservationStatus.Confirmed.toString(),
                )
            ReservationStatus.Confirmed ->
                listOf(
                    ReservationStatus.Confirmed.toString(),
                )
            ReservationStatus.Cancelled ->
                listOf(
                    ReservationStatus.Cancelled.toString(),
                )
        }
    }

    fun render(
        reservation: BoatSpaceReservationDetails,
        status: ReservationStatus
    ): String {
        val radioButtons =
            formComponents.radioButtons(
                "citizenDetails.reservationStatus",
                "reservationStatus",
                status.name,
                statusOptions(reservation.status).map {
                    RadioOption(if (it == notPaid) ReservationStatus.Info.toString() else it, t("citizenDetails.reservationStatus.$it"))
                },
                staticAttributesForOptions = mapOf("x-model" to "reservationStatus"),
            )

        return """<div data-testid="reservation-status-selector" >
             $radioButtons
           </div>
            """
    }
}
