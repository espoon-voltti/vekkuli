package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.Citizen
import fi.espoo.vekkuli.domain.CitizenMemoWithDetails
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.Icons
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

enum class SubTab {
    Reservations,
    Payments,
    Messages,
    Memos,
}

@Service
class CitizenDetails {
    @Autowired
    private lateinit var citizenService: CitizenService

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var icons: Icons

    fun t(key: String): String = messageUtil.getMessage(key)

    fun citizenPage(
        @SanitizeInput citizen: Citizen,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        @SanitizeInput errors: MutableMap<String, String>? = mutableMapOf(),
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
                            <p>${citizen.address ?: "-"} ${citizen.postalCode}</p>
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
                            <p>${citizen.municipality ?: "-"}</p>
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
            // language=HTML
            """
            <div class="tabs is-boxed container-tabs">
                <ul>
                    <li class="is-active"><a>${t("boatSpaceReservation.title.marineActivities")}</a></li>
                    <li><a>${t("boatSpaceReservation.title.spaceReservations")}</a></li>
                    <li><a>${t("boatSpaceReservation.title.guidedExercise")}</a></li>
                </ul>
            </div>
            """.trimIndent()

        val result =
            // language=HTML
            """
            <section class="section" id="citizen-details">
                <div class="container block">
                    <button class="icon-text">
                        <span class="icon">
                            <div>${icons.chevronLeft}</div>
                        </span>
                        <a href="/virkailija/venepaikat/varaukset">
                            <span>${t(
                "boatSpaces.goBack"
            )}</span>
                        </a>
                    </button>
                    <h2>${citizen.firstName + " " + citizen.lastName}</h2>
                </div>
                $customerInfo
                $tabs
                ${reservationTabContent(citizen, boatSpaceReservations, boats)}
            </section>
            """.trimIndent()

        return result
    }

    private fun getTabUrl(last: String): String = getServiceUrl("/virkailija/kayttaja/$last")

    fun reservationTabContent(
        @SanitizeInput citizen: Citizen,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
    ): String {
        // language=HTML
        val reservationList =
            boatSpaceReservations.joinToString("\n") { reservation ->
                """
                <div class="reservation-card">
                    <div class="columns is-vcentered">
                        <div class="column is-narrow">
                            <h4>${t("citizenDetails.boatSpace")}: ${reservation.locationName} ${reservation.place}</h4>
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
                                <p>03.06.2024 14:56</p> <!-- Placeholder for paid date -->
                            </div>
                        </div>
                    </div>
                </div>
                """.trimIndent()
            }

        fun showBoatWarnings(boatHasWarnings: Boolean): String {
            if (boatHasWarnings) {
                // language=HTML
                return """
                <div class="column">
                    <a class="is-link" x-on:click="modalOpen = true">
                        <span class="icon ml-s">
                            <span>${icons.warningExclamation(false)}</span>
                        </span>
                        <span>Kuittaa tiedot tarkistetuiksi</span>
                    </a>
                </div>
                """
            }
            return ""
        }

        fun showBoatWarnings(boat: CitizenUserController.BoatUpdateForm): String {
            // language=HTML

            if (boat.hasAnyWarnings()) {
                val warningLabels =
                    boat.warnings.joinToString("\n") { warning ->
                        """
                        <label class="radio">
                            <input type="radio" name="key" value="$warning">
                            ${t("reservationWarning.$warning")}
                        </label>
                        """.trimIndent()
                    }
                return """
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
                                            $warningLabels
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
                                    <div class="info-icon">${icons.warningExclamation(false)}</div>
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
                    </div>
                    """.trimIndent()
            }
            return ""
        }

        // language=HTML
        val boatsList =
            boats
                .mapIndexed { index, boat ->
                    """
                    <div class="reservation-card" id="boat-$index" x-data="{ modalOpen: false }">
                        <div class="columns is-vcentered">
                            <div class="column is-narrow">
                                <h4>${t("citizenDetails.boat")} ${boat.name}</h4>
                            </div>
                            <span class="memo-edit-buttons column columns">
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
                                ${showBoatWarnings(boat.hasAnyWarnings())}
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
                                <p id="boat-otherIdentifier-text-$index">${boat.otherIdentifier}</p>
                            </div>
                            <div class="column">
                                <label class="label">${t("boatSpaceReservation.title.additionalInfo")}</label>
                                <p id="boat-extraInformation-text-$index">
                                   ${if (!boat.extraInformation.isNullOrEmpty()) boat.extraInformation else "-"}
                                </p>
                            </div>
                        </div>
                        ${showBoatWarnings(boat)}
                    </div>
                    """.trimIndent()
                }.joinToString("\n")

        // language=HTML
        return """
            <div id="tab-content" class="container block">
                ${renderTabNavi(citizen.id, SubTab.Reservations)}
                <h3>${t("boatSpaceReservation.title.splitReservations")}</h3>
                <div class="reservation-list">
                    $reservationList
                </div>
                <h3>${t("boatSpaceReservation.title.boats")}</h3>
                <div class="reservation-container">
                    $boatsList
                </div>
            </div>
            """.trimIndent()
    }

    fun paymentTabContent(citizen: Citizen): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${renderTabNavi(citizen.id, SubTab.Payments)}
              <h3>PAYMENTS</h3>
            </div>
            """.trimIndent()
    }

    fun messageTabContent(citizen: Citizen): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${renderTabNavi(citizen.id, SubTab.Messages)}
              <h3>MESSAGES</h3>
            </div>
            """.trimIndent()
    }

    fun formatDate(d: LocalDateTime): String = d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

    fun memoContent(
        memo: CitizenMemoWithDetails,
        edit: Boolean
    ): String {
        val createdBy =
            if (memo.createdBy !== null) {
                """
                <span class="memo-label">${memo.createdBy}</span>
                """
            } else {
                ""
            }

        val header =
            """
            <div>
                <span class="memo-label">${formatDate(memo.createdAt)}</span>
                $createdBy
                <a hx-get="${getTabUrl("${memo.citizenId}/muistiinpanot/muokkaa/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#memo-${memo.id}"
                   hx-swap="outerHTML">
                    <span class="icon ml-s">
                        ${icons.edit}
                    </span>
                </a>
                <a hx-delete="${getTabUrl("${memo.citizenId}/muistiinpanot/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#tab-content"
                   hx-swap="outerHTML"
                   hx-confirm="Haluatko varmasti poistaa muistion?">
                    <span class="icon ml-s">
                        ${icons.remove}
                    </span>
                </a>
            </div>
            """.trimIndent()

        val updated =
            if (memo.updatedBy !== null && memo.updatedAt !== null) {
                """
                <div class="memo-updated-by">
                    Muokattu
                    <span>${formatDate(memo.updatedAt)}</span>
                    <span>${memo.updatedBy}</span>
                </div>
                """.trimIndent()
            } else {
                ""
            }

        val content =
            if (edit) {
                // language=HTML
                """
                <form hx-patch="${getTabUrl("${memo.citizenId}/muistiinpanot/${memo.id}")}"
                      hx-target="#memo-${memo.id}"
                      hx-swap="outerHTML">
                    <div class="control memo-edit-area">
                        <textarea class="textarea" rows="1" class="memo-content-input" name="content">${memo.content}</textarea>
                        <div class="memo-edit-buttons">
                            <button type="submit">
                                <span class="icon ml-s" 
                                      style='stroke: green;'>
                                    ${icons.check}
                                </span>
                            </button>
                            <span class="icon ml-s"
                                  hx-get="${getTabUrl("${memo.citizenId}/muistiinpanot/${memo.id}")}"
                                  hx-trigger="click"
                                  hx-target="#memo-${memo.id}"
                                  hx-swap="outerHTML">
                                ${icons.remove}
                            </span>
                        </div>
                    </div>
                </form>
                """.trimIndent()
            } else {
                // language=HTML
                """
                <div class="memo-content">
                    ${memo.content}
                </div>
                """
            }

        // language=HTML
        return """
            <div class="block memo" id="memo-${memo.id}">
               $header
               $updated
               $content
            </div>
            """.trimIndent()
    }

    fun newMemoContent(
        citizenId: UUID,
        edit: Boolean
    ): String {
        if (edit) {
            // language=HTML
            return """
                <div id="new-memo" class="block">
                    <form hx-post="${getTabUrl("$citizenId/muistiinpanot")}"
                        hx-target="#tab-content"
                        hx-swap="outerHTML">
                        <div class="memo-edit-area">
                            <div class="control">
                                <textarea class="textarea" rows="1" class="memo-content-input" name="content"></textarea>
                            </div>
                            <div class="memo-edit-buttons">
                                <button type="submit">
                                    <span class="icon ml-s" style='stroke: green;'>${icons.check}</span>
                                </button>
                                <a hx-get="${getTabUrl("$citizenId/muistiinpanot/lisaa_peruuta")}" 
                                    hx-trigger="click" 
                                    hx-target="#new-memo" 
                                    hx-swap="outerHTML" >
                                    <span class="icon ml-s">${icons.remove}</span>
                                </a>
                            </div> 
                        </div>
                    </form>
                </div>
                """.trimIndent()
        }
        // language=HTML
        return """
            <div id="new-memo" class="block">
                <a 
                    hx-get="${getTabUrl("$citizenId/muistiinpanot/lisaa")}"
                    hx-trigger="click"
                    hx-target="#new-memo"
                    hx-swap="outerHTML">
                    <span class="icon mr-s">${icons.plus}</span>
                    <span>Uusi muistiinpano</span>   
                </a>
            </div>
            """.trimIndent()
    }

    fun memoTabContent(
        citizenId: UUID,
        memos: List<CitizenMemoWithDetails>
    ): String {
        val memoHtml =
            memos.joinToString("\n") {
                memoContent(it, false)
            }

        val result =
            // language=HTML
            """
            <div id="tab-content" class="container block">
                ${renderTabNavi(citizenId, SubTab.Memos)}
                ${newMemoContent(citizenId, false)}
                $memoHtml
            <div>
            """.trimIndent()

        return result
    }

    fun tabCls(
        activeTab: SubTab,
        tab: SubTab
    ): String {
        if (activeTab == tab) return "is-active"
        return ""
    }

    fun renderTabNavi(
        citizenId: UUID,
        activeTab: SubTab
    ): String =
        // language=HTML
        """
        <div class="tabs is-boxed secondary-tabs">
            <ul>
                <li class="${tabCls(activeTab, SubTab.Reservations)}">
                    <a hx-get="${getTabUrl("$citizenId/varaukset")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.reservations")}</a>
                </li>
                <li class="${tabCls(activeTab, SubTab.Messages)}">
                    <a hx-get="${getTabUrl("$citizenId/viestit")}"
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML"> ${t("boatSpaceReservation.title.messages")}</a>
                </li>
                <li class="${tabCls(activeTab, SubTab.Payments)}">
                    <a hx-get="${getTabUrl("$citizenId/maksut")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.paymentHistory")}</a>
                </li>
               <li class="${tabCls(activeTab, SubTab.Memos)}">
                    <a hx-get="${getTabUrl("$citizenId/muistiinpanot")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.notes")}</a>
               </li>
            </ul>
        </div>

             
            
        
        """.trimIndent()
}
