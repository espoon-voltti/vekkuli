package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReservationCardInformation : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired
    lateinit var cardHeading: ReservationCardHeading

    @Autowired
    lateinit var trailerCard: TrailerCard

    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
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
                         <p>${reservation.boatSpaceWidthInM}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.reservationDate")}</label>
                         <p>${formatAsFullDate(reservation.startDate)}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.place")}</label>
                         <p ${addTestId("reservation-list-card-place")}>${reservation.place}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("shared.label.lengthInMeters")}</label>
                         <p>${reservation.boatSpaceLengthInM}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.label.reservationValidity")}</label>
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
                         <label class="label">${t("boatSpaceReservation.title.boatPresent")}</label>
                         <p>${reservation.boat?.name ?: ""}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         $amenityWrapper
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.paid")}</label>
                         <p id="paidFieldInfo">${formatAsFullDate(reservation.paymentDate)}</p> 
                     </div>
                 </div>
                 
             </div>
            ${reservation.trailer?.let { trailerCard.render(it)} ?: ""}

            """.trimIndent()
    }

    private fun renderReservationValidity(reservation: BoatSpaceReservationDetails): String {
        val reservationValidityText =
            if (reservation.terminationTimestamp != null) {
                renderWithTerminatedDate(reservation)
            } else {
                t(
                    "boatSpaceReservation.validity.${reservation.validity}",
                    listOf(formatAsFullDate(reservation.endDate))
                )
            }
        return reservationValidityText
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
