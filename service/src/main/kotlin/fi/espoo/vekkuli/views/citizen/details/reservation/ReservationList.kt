package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.modal.*
import fi.espoo.vekkuli.views.employee.SanitizeInput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReservationList : BaseView() {
    @Autowired
    lateinit var icons: Icons

    @Autowired private lateinit var formComponents: FormComponents

    @Autowired private lateinit var modal: Modal

    fun build(
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
                    ${modal.createOpenModalBuilder()
                        .addAttribute("id","invoice-paid-button")
                        .setText(t("citizenDetails.markInvoicePaid"))
                        .setPath("/reservation/modal/mark-invoice-paid/${reservation.id}/${citizen.id}")
                        .setStyle(ModalButtonStyle.Primary)
                        .build()
                    }
                    """.trimIndent()
                } else {
                    ""
                }
            }
                        <button class="button is-primary">
                            ${t("boatSpaceReservation.button.swapPlace")}
                        </button>
                        
                        ${modal.createOpenModalBuilder()
                .setText(t("boatSpaceReservation.button.terminateReservation"))
                .setPath("/reservation/modal/terminate-reservation/${reservation.id}")
                .setStyle(ModalButtonStyle.DangerOutline)
                .setTestId("open-terminate-reservation-modal")
                .build()
            }
                    </div>
                </div>
            """.trimIndent()
        }
    }
}
