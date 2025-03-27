package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import fi.espoo.vekkuli.views.components.modal.ModalButtonStyle
import fi.espoo.vekkuli.views.components.modal.OpenModalButtonType
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class ReservationCardInformation(
    private val trailerCard: TrailerCard,
    private val modal: Modal
) : BaseView() {
    fun render(
        reservation: BoatSpaceReservationDetails,
        reserverId: UUID,
    ): String {
        val amenity =
            if (reservation.type == BoatSpaceType.Slip) {
                t("boatSpaces.amenityOption.${reservation.amenity}")
            } else {
                t("boatSpaces.storageType.${reservation.storageType}")
            }

        val amenityLabel =
            if (reservation.type == BoatSpaceType.Slip) {
                t("boatSpaceReservation.title.equipment")
            } else {
                t("boatSpaces.storageTypeHeader")
            }

        val amenityWrapper =
            """ 
            <label class="label">$amenityLabel</label>
            <p>$amenity</p>
            """.trimIndent()

        val paymentStatus =
            when (reservation.status) {
                ReservationStatus.Confirmed ->
                    """
                    <div class="payment-status">
                        <span>${
                        t(
                            "citizenDetails.reservationStatus.Confirmed"
                        )
                    }, ${reservation.paymentDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: ""}</span>
                        <span>${t("citizenDetails.reservationStatus.infoText")}: ${reservation.paymentReference}</span>
                    </div>    
                    """.trimIndent()

                ReservationStatus.Invoiced ->
                    """
                    <div class="payment-status">
                        <span>${
                        t(
                            "citizenDetails.reservationStatus.InvoicedStatusText"
                        )
                    }: ${reservation.invoiceDueDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: ""}</span>
                        <span>${t("citizenDetails.reservationStatus.infoText")}: ${reservation.paymentReference}</span>
                    </div>    
                    """.trimIndent()

                ReservationStatus.Payment, ReservationStatus.Info -> t("citizenDetails.reservationStatus.NotPaid")
                ReservationStatus.Cancelled -> t("citizenDetails.reservationStatus.Cancelled")
            }

        val paymentEditLink =
            """
            <div>
                <a class="is-link is-icon-link edit-link"
                    id="update-payment-status-link"
                    data-testid="update-payment-status-link"
                    hx-get="/reservation/modal/update-payment-status/${reservation.id}/$reserverId"
                    hx-target="#modal-container"
                    hx-swap="innerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                </a>
            </div>
            """.trimIndent()

        val typeEditLink =
            """
            <div>
                <a class="is-link is-icon-link edit-link"
                    data-testid="update-reservation-validity-link"
                    hx-get="/reservation/modal/update-type/${reservation.id}/$reserverId"
                    hx-target="#modal-container"
                    hx-swap="innerHTML">
                    <span class="icon">
                        ${icons.edit}
                    </span>
                </a>
            </div>
            """.trimIndent()

        val boatChangeLink =
            modal
                .createOpenModalBuilder()
                .setType(OpenModalButtonType.Link)
                .setText("""<span class="icon">${icons.edit}</span>""")
                .setPath("/virkailija/venepaikat/varaukset/vaihda-vene/${reservation.id}")
                .setStyle(ModalButtonStyle.EditIcon)
                .setTestId("open-change-reservation-boat-modal")
                .build()

        // language=HTML
        return """
            <div class="columns">
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.harbor")}</label>
                         <p ${addTestId("reservation-list-card-location-name")} >${reservation.locationName}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("shared.label.widthInMeters")}</label>
                         <p>${formatDecimal(reservation.boatSpaceWidthInM)}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.reservationStartDate")}</label>
                         <p>${formatAsFullDate(reservation.startDate)}</p>
                         <span class="reservation-created-tooltip">Varaus tehty: ${formatAsFullDateTime(reservation.created)}<span>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.place")}</label>
                         <p ${addTestId("reservation-list-card-place")}>${reservation.place}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("shared.label.lengthInMeters")}</label>
                         <p>${formatDecimal(reservation.boatSpaceLengthInM)}</p>
                     </div>
                     <div class="field" ${addTestId("reservation-validity")}>
                         <div class="edit-label">
                             <label class="label">${t("boatSpaceReservation.label.reservationValidity")}</label>
                             $typeEditLink
                         </div>
                         <p>${renderReservationValidity(reservation)}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.placeType")}</label>
                         <p>${t("boatSpaces.typeOption.${reservation.type}")}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.payment")}</label>
                         <p>${reservation.priceInEuro}</p>
                     </div>
                     <div class="field">
                     <div class="edit-label">
                         <label class="label">${t("boatSpaceReservation.title.boatPresent")}</label>
                         <div>$boatChangeLink</div>
                     </div>
                         <p ${addTestId("reservation-list-card-boat")}>${reservation.boat?.name ?: ""}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         $amenityWrapper
                     </div>
                     <div class="field">
                         <div class="edit-label">
                           <label class="label">${t("citizenDetails.reservationStatus.paymentStatus")}</label>
                           $paymentEditLink
                         </div>
                         <div data-testid="payment-status">
                            $paymentStatus
                         </div>    
                     </div>
                 </div>
                 
             </div>
            ${reservation.trailer?.let { trailerCard.render(it, reserverId) } ?: ""}

            """.trimIndent()
    }

    private fun renderReservationValidity(reservation: BoatSpaceReservationDetails): String {
        if (reservation.terminationTimestamp != null) {
            return renderWithTerminatedDate(reservation)
        } else {
            var reservationValidityText =
                t(
                    "boatSpaceReservation.validity.${reservation.validity}",
                    listOf(formatAsFullDate(reservation.endDate))
                )

            // For indefinite reservations, show also the end date for employees
            if (reservation.validity == ReservationValidity.Indefinite) {
                reservationValidityText +=
                    " (${
                        t(
                            "boatSpaceReservation.validity.${ReservationValidity.FixedTerm}",
                            listOf(formatAsFullDate(reservation.endDate))
                        )
                    })"
            }
            return reservationValidityText
        }
    }

    private fun renderWithTerminatedDate(reservation: BoatSpaceReservationDetails): String =
        """
        ${formatAsFullDate(reservation.endDate)}
        </br>
        <span ${addTestId("reservation-list-card-terminated-date")}>
        ${t("boatSpaceReservation.terminated")} ${formatAsFullDate(reservation.terminationTimestamp?.toLocalDate())}
        </span>
        """.trimIndent()
}
