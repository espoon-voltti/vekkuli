package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import org.springframework.stereotype.Service

@Service
class ReservationConfirmation(
    private val messageUtil: MessageUtil,
    private val stepIndicator: StepIndicator,
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(reservation: BoatSpaceReservationDetails): String {
        // language=HTML
        return """
            <section class="section">
                <div class="box">
                    ${stepIndicator.render(2)}
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
                            <div>
                                <label> ${t("boatSpaceReservation.label.placeWidth")}</label>
                                <span>${reservation.boatSpaceWidthInM}</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.placeLength")}</label>
                                <span>${reservation.boatSpaceLengthInM}</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.placeEquipment")}</label>
                                <span>${t("boatSpaces.amenityOption." + reservation.amenity)}</span>
                            </div>
                        </div>
                        <div class="block">
                            <label>${t("boatSpaceReservation.label.price")}</label>
                            <div>
                                <label>${t("boatSpaceReservation.label.payment")}</label>
                                <span>${reservation.priceWithoutAlvInEuro} &euro;</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.vat")}</label> 
                                <span>${reservation.alvPriceInEuro} &euro;</span>
                            </div>
                            <div>
                                <label>${t("boatSpaceReservation.label.total")}</label>
                                <span>${reservation.priceInEuro} &euro;</span>
                            </div>
                        </div>
                        <div class="block">
                            <label>${t("boatSpaceReservation.label.reservationValidity")}</label>
                            <span>${t("boatSpaceReservation.validity.${reservation.validity}")}</span> 
                        </div>
                        <a class="button is-primary block" href="/kuntalainen/venepaikat">
                          ${t("boatSpaceReservation.button.backToHomePage")}</a>
                    </div>
                </div>
            </section>
            """.trimIndent()
    }
}
