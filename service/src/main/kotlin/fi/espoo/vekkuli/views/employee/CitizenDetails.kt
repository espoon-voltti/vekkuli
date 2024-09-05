package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.views.Icons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CitizenDetails {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var icons: Icons

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun citizenPage(
        citizen: Citizen,
        boatSpaceReservations: List<BoatSpaceReservationDetails>,
        boats: List<CitizenUserController.BoatUpdateForm>,
        errors: MutableMap<String, String>? = mutableMapOf(),
    ): String {
        val customerInfo =
            """
            <div class="container block">
                <div class="columns">
                    <div class="column is-narrow">
                        <h3 class="header">${t("boatSpaceReservation.title.customerInformation")}</h3>
                    </div>
                    <div class="column">
                        <!-- Placeholder for additional actions, if needed -->
                    </div>
                </div>
                <div class="columns">
                    <div class="column is-one-quarter">
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.name")}</label>
                            <p>${citizen.firstName} ${citizen.lastName}</p>
                        </div>
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.address")}</label>
                            <p>${citizen.address ?: '-'} ${citizen.postalCode}</p>
                        </div>
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.email")}</label>
                            <p>${citizen.email}</p>
                        </div>
                    </div>
                    <div class="column is-one-quarter">
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.ssn")}</label>
                            <p>${citizen.nationalId}</p>
                        </div>
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.municipality")}</label>
                            <p>${citizen.municipality ?: '-'}</p>
                        </div>
                        <div class="field">
                            <label class="label">${t("boatSpaceReservation.title.phoneNumber")}</label>
                            <p>${citizen.phone}</p>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent()

        val tabs =
            """
            <div class="tabs is-boxed container-tabs">
                <ul>
                    <li class="is-active"><a>${t("boatSpaceReservation.title.marineActivities")}</a></li>
                    <li><a>${t("boatSpaceReservation.title.spaceReservations")}</a></li>
                    <li><a>${t("boatSpaceReservation.title.guidedExercise")}</a></li>
                </ul>
            </div>
            """.trimIndent()

        // language=HTML
        val reservationList =
            boatSpaceReservations.joinToString("\n") { reservation ->
                """
                <div class="reservation-card">
                    <div class="columns is-vcentered">
                        <div class="column is-narrow">
                            <h4>${t("citizenDetails.boatSpace")} ${reservation.locationName} ${reservation.place}</h4>
                        </div>
                        <div class="column is-narrow">
                            <a class="is-link">
                                <span class="icon ml-s">
                                    ${icons.swap}
                                </span>
                                <span>${t("boatSpaceReservation.button.swapPlace")}</span>
                            </a>
                        </div>
                        <div class="column">
                            <a class="is-link">
                                <span class="icon ml-s">
                                    ${icons.cross}
                                </span>
                                <span>${t("boatSpaceReservation.button.terminateReservation")}</span>
                            </a>
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
                                <p>${reservation.boatName}</p>
                            </div>
                        </div>
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.equipment")}</label>
                                <p>${t("boatSpaces.amenityOption.${reservation.amenity}")}</p>
                            </div>
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.paid")}</label>
                                <p>03.06.2024 14:56</p> <!-- Placeholder for paid date -->
                            </div>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            }

        // language=HTML
        val boatsList =
            boats.mapIndexed { index, boat ->
                """
                <div class="reservation-card" id="boat-$index" x-data="{ modalOpen: false }">
                    <div class="columns is-vcentered">
                        <div class="column is-narrow">
                            <h4>${t("citizenDetails.boat")} ${boat.name}</h4>
                        </div>
                        <span class="edit-buttons column columns">
                            <div class="column is-narrow">
                                <a class="edit-link s-link"
                                   hx-get="/virkailija/kayttaja/${citizen.id}/vene/${boat.id}/muokkaa"
                                   hx-target="#boat-$index"
                                   hx-swap="innerHTML">
                                    <span class="icon ml-s">
                                        ${icons.edit}
                                    </span>
                                    <span id="edit-boat-$index"> ${t("boatSpaceReservation.button.editBoatDetails")}</span>
                                </a>
                            </div>
                            <div class="column">
                                <a class="is-link has-text-danger">
                                    <span class="icon ml-s">
                                        ${icons.remove}
                                    </span>
                                    <span>${t("boatSpaceReservation.button.deleteBoat")}</span>
                                </a>
                            </div>
                            ${
                    if (boat.hasAnyWarnings()) {
                        """
                        <div class="column">
                            <a class="is-link" x-on:click="modalOpen = true">
                                <span class="icon ml-s">
                                    <span>${icons.warningExclamation(false)}</span>
                                </span>
                                <span>Kuittaa tiedot tarkistetuiksi</span>
                            </a>
                        </div>
                        """
                    } else {
                        ""
                    }
                }
                        </span>
                    </div>
                    <div class="columns">
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.boatName")}</label>
                                <p id="boat-name-text-$index">${boat.name}</p>
                            </div>
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.weight")}</label>
                                <p  id="boat-weight-text-$index">${boat.weight}</p>
                            </div>
                        </div>
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.boatType")}</label>
                                <p  id="boat-type-text-$index">${t("boatApplication.boatTypeOption.${boat.type}")}</p>
                            </div>
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.draft")}</label>
                                <p id="boat-depth-text-$index">${boat.depth}</p>
                            </div>
                        </div>
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.boatWidth")}</label>
                                <p  id="boat-width-text-$index">${boat.width}</p>
                            </div>
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.registrationNumber")}</label>
                                <p  id="boat-registrationNumber-text-$index">${boat.registrationNumber}</p>
                            </div>
                        </div>
                        <div class="column">
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.boatLength")}</label>
                                <p  id="boat-length-text-$index">${boat.length}</p>
                            </div>
                            <div class="field">
                                <label class="label">${t("boatSpaceReservation.title.ownershipStatus")}</label>
                                <p id="boat-ownership-text-$index">${t("boatApplication.ownershipOption.${boat.ownership}")}</p>
                            </div>
                        </div>
                    </div>
                     <div class="columns">
                        <div class="column is-one-quarter">
                            <label class="label" th:text="#{boatSpaceReservation.title.otherIdentifier}">Muu
                                tunniste</label>
                            <p id="boat-otherIdentifier-text-$index">${boat.otherIdentifier ?: "-"}</p>
                        </div>
                        <div class="column">
                            <label class="label">${t("boatSpaceReservation.title.additionalInfo")}</label>
                            <p id="boat-extraInformation-text-$index">
                               ${if (!boat.extraInformation.isNullOrEmpty()) boat.extraInformation else "-"}
                            </p>
                        </div>
                    </div>
                    ${
                    if (boat.hasAnyWarnings()) {
                        """
                <div class="modal" x-show="modalOpen" style="display:none;">
                    <div class="modal-underlay" @click="modalOpen = false"></div>
                    <div class="modal-content">
                        <form hx-post="/virkailija/venepaikat/varaukset/kuittaa-varoitus"
                              hx-swap="none"
                              x-on:htmx:after-request="modalOpen = false">
                            <input type="hidden" name="boatId" value="${boat.id}" />
                            <input type="hidden" name="reservationId" value="${boat.reservationId}" />
                            <div class="block">
                                <div class="field">
                                    <h1 class="label">Valitse kuitattava tieto</h1>
                                    <div class="control">
                                        ${
                            boat.warnings.joinToString("\n") { warning ->
                                """
                                <label class="radio">
                                    <input type="radio" name="key" value="$warning">
                                    ${t("reservationWarning.$warning")}
                                </label>
                                """.trimIndent()
                            }
                        }
                                    </div>
                                </div>
                            </div>
                            <div class="block">
                                <h1 class="label">Lisätietoa kuittauksesta</h1>
                                <div class="control">
                                    <textarea class="textarea" rows="1"></textarea>
                                </div>
                            </div>
                            <div class="ack-info">
                                <div class="info-icon"><!-- Icon for warning --></div>
                                <div class="info-content">
                                    ${t("reservationWarning.ackInfo")}
                                </div>
                            </div>
                            <div class="block">
                                <button id="ack-modal-cancel"
                                        class="button"
                                        x-on:click="modalOpen = false"
                                        type="button">
                                    ${t("cancel")}
                                </button>
                                <button
                                        id="ack-modal-confirm"
                                        class="button is-primary"
                                        type="submit">
                                    ${t("confirm")}
                                </button>
                            </div>
                        </form>
                    </div>
                """
                    } else {
                        ""
                    }
                }
                </div>
                """.trimIndent()
            }.joinToString("\n")

        // language=HTML
        return """
            <section class="section" id="citizen-details">
                <div class="container block">
                    <button class="icon-text">
                        <span class="icon">
                            <div><!-- Icon for back --></div>
                        </span>
                        <a href="/virkailija/venepaikat/varaukset">
                            <span>${t("boatSpaces.goBack")}</span>
                        </a>
                    </button>
                    <h2>${citizen.firstName} ${citizen.lastName}</h2>
                </div>
                
                $customerInfo
                $tabs

                <!-- Reservations section -->
                <div class="container block">
                    <div class="tabs is-boxed secondary-tabs">
                        <ul>
                            <li class="is-active"><a>${t("boatSpaceReservation.title.reservations")}</a></li>
                            <li><a>${t("boatSpaceReservation.title.messages")}</a></li>
                            <li><a>${t("boatSpaceReservation.title.paymentHistory")}</a></li>
                            <li><a>${t("boatSpaceReservation.title.notes")}</a></li>
                        </ul>
                    </div>
                    <h3>${t("boatSpaceReservation.title.splitReservations")}</h3>
                    <div class="reservation-list">
                        $reservationList
                    </div>
                    
                    <!-- Boats -->
                    <div class="reservation-container">
                        $boatsList
                    </div>
                </div>
            </section>
            """.trimIndent()
    }
}
