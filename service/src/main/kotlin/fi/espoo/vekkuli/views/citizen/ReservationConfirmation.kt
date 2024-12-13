package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.utils.formatAsFullDate
import org.springframework.stereotype.Service

@Service
class ReservationConfirmation(
    private val messageUtil: MessageUtil,
    private val stepIndicator: StepIndicator,
) {
    fun t(
        key: String,
        params: List<String> = emptyList()
    ): String = messageUtil.getMessage(key, params)

    fun render(reservation: BoatSpaceReservationDetails): String {
        val widthAndLength =
            if (reservation.amenity != BoatSpaceAmenity.Buoy) {
                """
                <div>
                    <label> ${t("boatSpaceReservation.label.placeWidth")}</label>
                    <span>${reservation.boatSpaceWidthInM}</span>
                </div>
                <div>
                    <label>${t("boatSpaceReservation.label.placeLength")}</label>
                    <span>${reservation.boatSpaceLengthInM}</span>
                </div>
                """.trimIndent()
            } else {
                ""
            }

        // language=HTML
        val validityText =
            t(
                "boatSpaceReservation.validity.${reservation.validity}",
                listOf(formatAsFullDate(reservation.endDate))
            )

        val amenity =
            if (reservation.type == BoatSpaceType.Slip || reservation.amenity != BoatSpaceAmenity.None) {
                """
                <div>
                    <label>${t("boatSpaceReservation.label.placeEquipment")}</label>
                    <span>${t("boatSpaces.amenityOption." + reservation.amenity)}</span>
                </div>
                """.trimIndent()
            } else {
                ""
            }

        return """
            <section class="section">
                <div class="box">
                    ${stepIndicator.render(4)}
                    <div id="reservation-confirmation-container" class="block">
                        <h2>${t("boatSpaceReservation.title.confirmation")}</h2>
                        <div class="block">
                            ${t("boatSpaceReservation.message.successfulReservation")}
                        </div>
                        <div class="block">
                            <label>${t("boatSpaceReservation.label.placeDetails")}</label>
                            <div>
                                <label>${t("boatSpaceReservation.label.placeName")} </label>
                                <span>${reservation.locationName} ${reservation.place}</span>
                            </div>
                           $widthAndLength
                           $amenity
            
                        </div>
                        <div class="block">
                            <label>${t("boatSpaceReservation.label.price")}</label>
                            <div>
                                <label>${t("boatSpaceReservation.label.payment")}</label>
                                <span>${reservation.priceWithoutVatInEuro} &euro;</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.vat")}</label> 
                                <span>${reservation.vatPriceInEuro} &euro;</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.total")}</label>
                                <span>${reservation.priceInEuro} &euro;</span>
                            </div>
                        </div>
                        <div class="block">
                            <label>${t("boatSpaceReservation.label.reservationValidity")}</label>
                            <span>$validityText</span> 
                        </div>
                        <a class="button is-primary block" href="/kuntalainen/venepaikat" id="back-to-home-page">
                          ${t("boatSpaceReservation.button.backToHomePage")}</a>
                    </div>
                </div>
            </section>
            """.trimIndent()
    }
}
