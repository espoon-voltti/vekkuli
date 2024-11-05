package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReservationCardInformation : BaseView() {
    @Autowired
    lateinit var icons: Icons

    fun render(
        @SanitizeInput reservation: BoatSpaceReservationDetails,
    ): String {
        // language=HTML
        return """
            <div class="columns">
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.harbor")}</label>
                         <p ${addTestId("reservation-list-card-location-name")} >${reservation.locationName}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.width")}</label>
                         <p>${reservation.boatSpaceWidthInM}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.reservationDate")}</label>
                         <p>${reservation.startDate}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.place")}</label>
                         <p ${addTestId("reservation-list-card-place")}>${reservation.place}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.length")}</label>
                         <p>${reservation.boatSpaceLengthInM}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.contractValidity")}</label>
                         <p>${reservation.endDate}</p>
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
                         <p>${reservation.boatName ?: ""}</p>
                     </div>
                 </div>
                 <div class="column">
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.equipment")}</label>
                         <p>${t("boatSpaces.amenityOption.${reservation.amenity}")}</p>
                     </div>
                     <div class="field">
                         <label class="label">${t("boatSpaceReservation.title.paid")}</label>
                         <p id="paidFieldInfo">${reservation.paymentDate ?: "-"}</p> 
                     </div>
                 </div>
             </div>
            """.trimIndent()
    }
}
