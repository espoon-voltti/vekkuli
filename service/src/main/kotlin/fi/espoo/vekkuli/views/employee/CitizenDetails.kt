package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.BoatSpaceReservationDetails
import fi.espoo.vekkuli.domain.CitizenMemoWithDetails
import fi.espoo.vekkuli.domain.SentMessage
import fi.espoo.vekkuli.views.CommonComponents
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
    private lateinit var formComponents: FormComponents

    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var icons: Icons

    @Autowired
    lateinit var commonComponents: CommonComponents

    fun t(key: String): String = messageUtil.getMessage(key)

    fun citizenPage(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        @SanitizeInput errors: MutableMap<String, String>? = mutableMapOf(),
    ): String {
        // language=HTML
        fun customerInfo(): String {
            val firstNameValue =
                formComponents.field(
                    "boatSpaceReservation.title.firstName",
                    "firstNameField",
                    citizen.firstName,
                )
            val lastNameValue =
                formComponents.field(
                    "boatSpaceReservation.title.lastName",
                    "lastNameField",
                    citizen.lastName,
                )
            val nationalIdValue =
                formComponents.field(
                    "boatSpaceReservation.title.nationalId",
                    "nationalIdField",
                    citizen.nationalId,
                )
            val addressValue = formComponents.field("boatSpaceReservation.title.address", "addressField", citizen.address)
            val postalCodeValue =
                formComponents.field("boatSpaceReservation.title.postalCode", "postalCodeField", citizen.postalCode)
            val cityValue = formComponents.field("boatSpaceReservation.title.city", "cityField", citizen.municipalityName)
            val municipalityValue =
                formComponents.field(
                    "boatSpaceReservation.title.municipality",
                    "municipalityCodeField",
                    citizen.municipalityName
                )
            val phoneNumberValue =
                formComponents.field("boatSpaceReservation.title.phoneNumber", "phoneNumberField", citizen.phone)
            val emailValue = formComponents.field("boatSpaceReservation.title.email", "emailField", citizen.email)
            return (
                """
                <div class="container block" id="citizen-information">
                    <div class="columns">
                        <div class="column is-narrow">
                            <h3 class="header">${t("boatSpaceReservation.title.customerInformation")}</h3>
                        </div>
                        <div class="column">
                            <div>
                                <a class="is-link" 
                                    id="edit-customer"
                                    hx-get="/virkailija/kayttaja/${citizen.id}/muokkaa"
                                    hx-target="#citizen-information"
                                    hx-swap="innerHTML">
                                    <span class="icon ml-s">
                                        ${icons.edit}
                                    </span>
                                    <span>${t("boatSpaceReservation.button.editCustomerDetails")}</span>
                                </a>
                            </div>
                            <!-- Placeholder for additional actions, if needed -->
                        </div>
                    </div>
                    ${
                    commonComponents.getCitizenFields(
                        firstNameValue,
                        lastNameValue,
                        nationalIdValue,
                        addressValue,
                        postalCodeValue,
                        cityValue,
                        municipalityValue,
                        phoneNumberValue,
                        emailValue
                    )
                }
            </div> 
            """
            )
        }

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
                            <span>${t("boatSpaces.goBack")}</span>
                        </a>
                    </button>
                    <h2>${citizen.firstName + " " + citizen.lastName}</h2>
                </div>
                ${customerInfo()}
                $tabs
                ${reservationTabContent(citizen, boatSpaceReservations, boats)}
            </section>
            """.trimIndent()

        return result
    }

    private fun getTabUrl(last: String): String = getServiceUrl("/virkailija/kayttaja/$last")

    fun reservationTabContent(
        @SanitizeInput citizen: CitizenWithDetails,
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

        fun deleteButton(
            hasLinkedReservation: Boolean,
            boatId: Int,
        ): String {
            if (!hasLinkedReservation) {
                return (
                    """
                    <div class="column" x-data="{deleteModal: false}">
                        <a class="is-link has-text-danger"
                            id='delete-boat-$boatId'
                           x-on:click="deleteModal = true">
                            <span class="icon ml-s">
                                ${icons.remove}
                            </span>
                            <span > ${t("boatSpaceReservation.button.deleteBoat")} </span>
                        </a>
                        <div class="modal" x-show="deleteModal" style="display:none;">
                            <div class="modal-underlay" @click="deleteModal = false"></div>
                            <div class="modal-content">
                                <div class="container">
                                    <div class="has-text-centered is-1">
                                        <p class='mb-m'>${t("boatSpaceReservation.text.deleteBoatConfirmation")}</p>
                                        <div class="buttons is-centered">
                                            <a class="button is-secondary" id="delete-modal-cancel-$boatId" x-on:click="deleteModal = false">${
                        t(
                            "cancel"
                        )
                    }</button>
                                            <a class="button is-danger" id="delete-modal-confirm-$boatId" hx-delete="/virkailija/kayttaja/${citizen.id}/vene/$boatId/poista">
                                                ${t("boatSpaceReservation.button.confirmDeletion")}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    """
                )
            }
            return ""
        }

        // language=HTML
        fun getBoatsList(boats: List<CitizenUserController.BoatUpdateForm>): String =
            boats
                .mapIndexed { _, boat ->

                    """
                    <div class="reservation-card" id="boat-${boat.id}" x-data="{ modalOpen: false }">
                        <div class="columns is-vcentered">
                            <div class="column is-narrow">
                                <h4>${t("citizenDetails.boat")} ${boat.name}</h4>
                            </div>
                            <span class="memo-edit-buttons column columns">
                                <div class="column is-narrow">
                                    <a class="edit-link s-link"
                                       hx-get="/virkailija/kayttaja/${citizen.id}/vene/${boat.id}/muokkaa"
                                       hx-target="#boat-${boat.id}"
                                       hx-swap="innerHTML">
                                        <span class="icon ml-s">
                                            ${icons.edit}
                                        </span>
                                        <span id="edit-boat-${boat.id}"> ${t("boatSpaceReservation.button.editBoatDetails")}</span>
                                    </a>
                                </div>
                                ${deleteButton(boat.reservationId != null, boat.id)}
                                
                                ${showBoatWarnings(boat.hasAnyWarnings())}
                                </span>
                        </div>
                        <div class="columns">
                            <div class="column">
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.boatName")}</label>
                                    <p id="boat-name-text-${boat.id}">${boat.name}</p>
                                </div>
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.weight")}</label>
                                    <p  id="boat-weight-text-${boat.id}">${boat.weight}</p>
                                </div>
                            </div>
                            <div class="column">
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.boatType")}</label>
                                    <p  id="boat-type-text-${boat.id}">${t("boatApplication.boatTypeOption.${boat.type}")}</p>
                                </div>
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.draft")}</label>
                                    <p id="boat-depth-text-${boat.id}">${boat.depth}</p>
                                </div>
                            </div>
                            <div class="column">
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.boatWidth")}</label>
                                    <p  id="boat-width-text-${boat.id}">${boat.width}</p>
                                </div>
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.registrationNumber")}</label>
                                    <p  id="boat-registrationNumber-text-${boat.id}">${boat.registrationNumber}</p>
                                </div>
                            </div>
                            <div class="column">
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.boatLength")}</label>
                                    <p  id="boat-length-text-${boat.id}">${boat.length}</p>
                                </div>
                                <div class="field">
                                    <label class="label">${t("boatSpaceReservation.title.ownershipStatus")}</label>
                                    <p id="boat-ownership-text-${boat.id}">${t("boatApplication.ownershipOption.${boat.ownership}")}</p>
                                </div>
                            </div>
                        </div>
                         <div class="columns">
                            <div class="column is-one-quarter">
                                <label class="label">${t("boatSpaceReservation.title.otherIdentifier")}</label>
                                <p id="boat-otherIdentifier-text-${boat.id}">${boat.otherIdentifier}</p>
                            </div>
                            <div class="column">
                                <label class="label">${t("boatSpaceReservation.title.additionalInfo")}</label>
                                <p id="boat-extraInformation-text-${boat.id}">
                                   ${if (!boat.extraInformation.isNullOrEmpty()) boat.extraInformation else "-"}
                                </p>
                            </div>
                        </div>
                        ${showBoatWarnings(boat)}
                    </div>
                    """.trimIndent()
                }.joinToString("\n")

        val boatsWithNoReservation = getBoatsList(boats.filter { it.reservationId == null })

        // language=HTML
        val showAllBoatsCheckbox =
            if (boatsWithNoReservation.isNotEmpty()) {
                """
                            <label class="checkbox pb-l">
                                <input type="checkbox"
                                name="showAllBoats"
                                id="showAllBoats"
                                x-model="showAllBoats"
                                hx-preserve="true"
                                x-ref="showAllBoats"
                                />
                                <span>${t("boatSpaceReservation.checkbox.showAllBoats")}</span>
                            </label>
                """
            } else {
                ""
            }

        // language=HTML
        return """
                   <div id="tab-content" class="container block" x-data="{ 
                showAllBoats: document.getElementById('showAllBoats').checked 
            }">
                       ${renderTabNavi(citizen.id, SubTab.Reservations)}
                       <h3>${t("boatSpaceReservation.title.splitReservations")}</h3>
                       <div class="reservation-list">
                           $reservationList
                       </div>
                       <h3>${t("boatSpaceReservation.title.boats")}</h3>
                       <div class="reservation-list no-bottom-border">
                           ${getBoatsList(boats.filter { it.reservationId != null })} 
                       </div>
                     
                      <div>
                         $showAllBoatsCheckbox
                          <div class="reservation-list" x-show="showAllBoats">    
                            ${getBoatsList(boats.filter { it.reservationId == null })} 
                           </div>
                      </div>
                   </div>
            """.trimIndent()
    }

    fun paymentTabContent(citizen: CitizenWithDetails): String {
        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${renderTabNavi(citizen.id, SubTab.Payments)}
              <h3>PAYMENTS</h3>
            </div>
            """.trimIndent()
    }

    fun messageTabContent(
        citizen: CitizenWithDetails,
        messages: List<SentMessage>,
    ): String {
        val messageHtml =
            messages.joinToString("\n") { message ->
                // language=HTML
                """
                <tr>
                    <td>${message.subject}</td>
                    <td>${message.recipientAddress}</td>
                    <td>${message.sentAt?.let { formatDate(it) } ?: "Ei lähetetty"}</td>
                    <td>${message.senderAddress ?: ""}</td>
                </tr>
                """.trimIndent()
            }

        val messagesHtml =
            if (messages.isNotEmpty()) {
                // language=HTML
                """
                <div class="message-list">
                    <table id="messages-table">
                      <thead>
                        <tr>
                          <th>${t("citizenDetails.messages.subject")}</th>
                          <th>${t("citizenDetails.messages.recipient")}</th>
                          <th>${t("citizenDetails.messages.sentAt")}</th>
                          <th>${t("citizenDetails.messages.sender")}</th>
                        </tr>
                      </thead>
                      <tbody>
                          $messageHtml
                      </tbody>
                    </table>
                </div>
                """.trimIndent()
            } else {
                "<h2>${t("citizenDetails.messages.noMessages")}</h2>"
            }

        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${renderTabNavi(citizen.id, SubTab.Messages)}
              $messagesHtml
            </div>
            """.trimIndent()
    }

    fun formatDate(d: LocalDateTime): String = d.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

    fun memoContent(
        memo: CitizenMemoWithDetails,
        edit: Boolean,
    ): String {
        val createdBy =
            if (memo.createdBy !== null) {
                """
                <span class="memo-label">${memo.createdBy}</span>
                """
            } else {
                ""
            }

        val buttons =
            if (edit) {
                ""
            } else {
                """
                <a id="edit-memo-button"
                   hx-get="${getTabUrl("${memo.citizenId}/muistiinpanot/muokkaa/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#memo-${memo.id}"
                   hx-swap="outerHTML">
                    <span class="icon ml-m">
                        ${icons.edit}
                    </span>
                </a>
                <a id="delete-memo-button"
                   hx-delete="${getTabUrl("${memo.citizenId}/muistiinpanot/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#tab-content"
                   hx-swap="outerHTML"
                   hx-confirm="${t("citizenDetails.removeMemoConfirm")}">
                    <span class="icon ml-m">
                        ${icons.remove}
                    </span>
                </a>
                """.trimIndent()
            }

        val header =
            """
            <div>
                <span class="memo-label">${formatDate(memo.createdAt)}</span>
                $createdBy
                $buttons
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
                        <textarea id="edit-memo-content" 
                                  class="textarea" 
                                  rows="1" 
                                  class="memo-content-input" 
                                  name="content">${memo.content}</textarea>
                        <div class="memo-edit-buttons">
                            <button id="save-edit-button" type="submit">
                                <span class="icon ml-m" 
                                      style='stroke: green;'>
                                    ${icons.check}
                                </span>
                            </button>
                            <span id="cancel-edit-button" class="icon ml-m"
                                  hx-get="${getTabUrl("${memo.citizenId}/muistiinpanot/${memo.id}")}"
                                  hx-trigger="click"
                                  hx-target="#memo-${memo.id}"
                                  hx-swap="outerHTML">
                                ${icons.xMark}
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
        edit: Boolean,
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
                                <textarea id="new-memo-content" 
                                          class="textarea" 
                                          rows="1" 
                                          class="memo-content-input" 
                                          name="content"></textarea>
                            </div>
                            <div class="memo-edit-buttons">
                                <button id="new-memo-save-button" type="submit">
                                    <span class="icon ml-m" style='stroke: green;'>${icons.check}</span>
                                </button>
                                <a id="new-memo-cancel-button" 
                                   hx-get="${getTabUrl("$citizenId/muistiinpanot/lisaa_peruuta")}" 
                                    hx-trigger="click" 
                                    hx-target="#new-memo" 
                                    hx-swap="outerHTML" >
                                    <span class="icon ml-m">${icons.xMark}</span>
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
                    id="add-new-memo"
                    hx-get="${getTabUrl("$citizenId/muistiinpanot/lisaa")}"
                    hx-trigger="click"
                    hx-target="#new-memo"
                    hx-swap="outerHTML">
                    <span class="icon mr-s">${icons.plus}</span>
                    <span>${t("citizenDetails.newMemo")}</span>   
                </a>
            </div>
            """.trimIndent()
    }

    fun memoTabContent(
        citizenId: UUID,
        memos: List<CitizenMemoWithDetails>,
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
        tab: SubTab,
    ): String {
        if (activeTab == tab) return "is-active"
        return ""
    }

    fun renderTabNavi(
        citizenId: UUID,
        activeTab: SubTab,
    ): String =
        // language=HTML
        """
        <div class="tabs is-boxed secondary-tabs">
            <ul>
                <li class="${tabCls(activeTab, SubTab.Reservations)}">
                    <a id="reservations-tab-navi"
                       hx-get="${getTabUrl("$citizenId/varaukset")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.reservations")}</a>
                </li>
                <li class="${tabCls(activeTab, SubTab.Messages)}">
                    <a id="messages-tab-navi"
                       hx-get="${getTabUrl("$citizenId/viestit")}"
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML"> ${t("boatSpaceReservation.title.messages")}</a>
                </li>
                <li class="${tabCls(activeTab, SubTab.Payments)}">
                    <a id="payments-tab-navi"
                       hx-get="${getTabUrl("$citizenId/maksut")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.paymentHistory")}</a>
                </li>
               <li class="${tabCls(activeTab, SubTab.Memos)}">
                    <a id="memos-tab-navi"
                       hx-get="${getTabUrl("$citizenId/muistiinpanot")}" 
                       hx-target="#tab-content" 
                       hx-trigger="click" 
                       hx-swap="outerHTML">${t("boatSpaceReservation.title.notes")}</a>
               </li>
            </ul>
        </div>

             
            
        
        """.trimIndent()
}
