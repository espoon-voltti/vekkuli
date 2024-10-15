package fi.espoo.vekkuli.views.citizen.details

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class Reservations : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired
    private lateinit var formComponents: FormComponents

    fun reservationList(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
    ): String {
        // language=HTML
        return boatSpaceReservations.joinToString("\n") { reservation ->
            """
                <div class="reservation-card" x-data="{modalOpen: false}">
                    <div class="columns is-vcentered">
                        <div class="column is-narrow">
                            <h4>${t("citizenDetails.boatSpace")}: ${reservation.locationName} ${reservation.place}</h4>
                        </div>
                    </div>
                   <div class="columns">
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.harbor")}</label>
                                <p>${reservation.locationName}</p>
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
                                <p>${reservation.place}</p>
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
                                <p></p> 
                            </div>
                        </div>
                    </div>
                    <div class="buttons">
                    ${
                if (reservation.status == ReservationStatus.Invoiced) {
                    """
                                <button class="button is-primary" id="invoice-paid-button" @click="modalOpen=true">
                                    ${t("citizenDetails.markInvoicePaid")}
                                </button>
                                ${modalInvoicePaid(citizen, reservation)}
                    """.trimMargin()
                } else {
                    ""
                }
            }
                        <button class="button is-primary">
                            ${t("boatSpaceReservation.button.swapPlace")}
                        </button>
                        
                         <button class="button is-danger is-outlined">
                            ${t("boatSpaceReservation.button.terminateReservation")}
                        </button>
                    </div>
                </div>
            """.trimIndent()
        }
    }

    private fun modalInvoicePaid(
        citizen: CitizenWithDetails,
        reservation: BoatSpaceReservationDetails
    ): String {
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        return """
                <div class="modal" x-show="modalOpen" style="display:none;">
                <div class="modal-underlay" @click="modalOpen = false"></div>
                <div class="modal-content">
                <h3>${t("citizenDetails.markInvoicePaid")}</h3>
                <form 
                    hx-post="/virkailija/venepaikat/varaukset/merkitse-maksu-suoritetuksi" 
                    hx-target="#citizen-details"
                    hx-select="#citizen-details"
                    hx-swap="outerHTML"
                    >
                    ${formComponents.textInput("citizenDetails.info", "invoicePaidInfo", "")}
                    ${formComponents.dateInput("citizenDetails.paymentDate", "paymentDate", today)}
                    <input hidden name="reservationId" value="${reservation.id}" />
                    <input hidden name="citizenId" value="${citizen.id}" />
                
                    <div class="block">
                        <button id="invoice-modal-cancel"
                                class="button"
                                x-on:click="modalOpen = false"
                                type="button">
                            ${t("cancel")}
                        </button>
                        <button
                                id="invoice-modal-confirm"
                                class="button is-primary"
                                type="submit">
                            ${t("confirm")}
                        </button>
                    </div>
                </form>
                </div>
            </div>
            """.trimIndent()
    }
}
