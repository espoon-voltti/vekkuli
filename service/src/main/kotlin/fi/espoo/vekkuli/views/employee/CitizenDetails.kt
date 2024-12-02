package fi.espoo.vekkuli.views.employee

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationList
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.components.WarningBox
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

enum class SubTab {
    Reservations,
    Payments,
    Messages,
    Memos,
}

@Service
class CitizenDetails(
    private val formComponents: FormComponents,
    private val reservationListBuilder: ReservationList,
    private val icons: Icons,
    private val commonComponents: CommonComponents,
    private val warningBox: WarningBox,
) : BaseView() {
    fun citizenPage(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        userType: UserType,
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
            val addressValue = formComponents.field("boatSpaceReservation.title.address", "addressField", citizen.streetAddress)
            val postalCodeValue =
                formComponents.field("boatSpaceReservation.title.postalCode", "postalCodeField", citizen.postalCode)
            val cityValue = formComponents.field("boatSpaceReservation.title.city", "cityField", citizen.postOffice)
            val municipalityValue =
                formComponents.field(
                    "boatSpaceReservation.title.municipality",
                    "municipalityCodeField",
                    citizen.municipalityName
                )
            val phoneNumberValue =
                formComponents.field("boatSpaceReservation.title.phoneNumber", "phoneNumberField", citizen.phone)
            val emailValue = formComponents.field("boatSpaceReservation.title.email", "emailField", citizen.email)
            val editUrl =
                if (userType == UserType.EMPLOYEE) {
                    "/virkailija/kayttaja/${citizen.id}/muokkaa"
                } else {
                    "/kuntalainen/kayttaja/muokkaa"
                }
            return (
                """
                <div class="container block" id="citizen-information">
                    <div class="columns">
                        <div class="column is-narrow">
                            <h3 class="header">${t("boatSpaceReservation.title.customerInformation")}</h3>
                        </div>
                        <div class="column">
                            <div>
                                <a class="is-link is-icon-link" 
                                    id="edit-customer"
                                    hx-get="$editUrl"
                                    hx-target="#citizen-information"
                                    hx-swap="innerHTML">
                                    <span class="icon">
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

        val backUrl =
            if (userType == UserType.EMPLOYEE) {
                "/virkailija/venepaikat/varaukset"
            } else {
                "/"
            }
        val result =
            // language=HTML
            """
            <section class="section" id="citizen-details">
                <div class="container block">
                    ${commonComponents.goBackButton(backUrl)} 
                    <h2>${citizen.firstName + " " + citizen.lastName}</h2>
                </div>
                ${customerInfo()}
                ${reservationTabContent(citizen, boatSpaceReservations, boats, userType)}
            </section>
            """.trimIndent()

        return result
    }

    private fun getTabUrl(last: String): String = getServiceUrl("/virkailija/kayttaja/$last")

    fun reservationTabContent(
        @SanitizeInput citizen: CitizenWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        userType: UserType
    ): String {
        val reservationList = reservationListBuilder.render(citizen, boatSpaceReservations, userType)

        fun showBoatWarnings(boatHasWarnings: Boolean): String {
            if (boatHasWarnings) {
                // language=HTML
                return """
                <div class="column">
                    <a class="is-link is-icon-link has-text-warning has-text-weight-semibold" x-on:click="modalOpen = true">
                        <span class="icon ml-s">
                            ${icons.warningExclamation(false)}
                        </span>
                        <span data-testid='acknowledge-warnings'>${t("citizenDetails.button.acknowledgeWarnings")}</span>
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
                                    <h1 class="label">${t("citizenDetails.label.warningInfo")}</h1>
                                    <div class="control">
                                        <textarea data-testid="warning-info-input" class="textarea" rows="1" name="infoText"></textarea>
                                    </div>
                                </div>
                                ${warningBox.render(t("reservationWarning.ackInfo"))}
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

        fun getDeleteUrl(boatId: Int): String {
            if (userType == UserType.EMPLOYEE) {
                return "/virkailija/kayttaja/${citizen.id}/vene/$boatId/poista"
            }
            return "/kuntalainen/vene/$boatId/poista"
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
                                            <a class="button is-secondary" id="delete-modal-cancel-$boatId" x-on:click="deleteModal = false">
                                                ${t("cancel")}
                                            </a>
                                            <a class="button is-danger" 
                                                id="delete-modal-confirm-$boatId" 
                                                hx-delete="${getDeleteUrl(boatId)}"
                                                hx-select="#citizen-details"
                                                hx-target="#citizen-details">
                                                ${t("boatSpaceReservation.button.confirmDeletion")}
                                            </a>
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

        val getEditUrl = { boatId: Int ->
            if (userType == UserType.EMPLOYEE) {
                "/virkailija/kayttaja/${citizen.id}/vene/$boatId/muokkaa"
            } else {
                "/kuntalainen/vene/$boatId/muokkaa"
            }
        }

        fun editBoatButton(boat: CitizenUserController.BoatUpdateForm): String {
            // language=HTML
            return """
                <div class="column is-narrow ml-auto">
                    <a class="is-icon-link is-link"
                       hx-get="${getEditUrl(boat.id)}"
                       hx-target="#boat-${boat.id}"
                       hx-swap="innerHTML">
                        <span class="icon">
                            ${icons.edit}
                        </span>
                        <span id="edit-boat-${boat.id}"> ${t("boatSpaceReservation.button.editBoatDetails")}</span>
                    </a>
                </div>
                """.trimIndent()
        }

        // language=HTML
        fun getBoatsList(
            boats: List<CitizenUserController.BoatUpdateForm>,
            isEmployee: Boolean
        ): String =
            boats
                .mapIndexed { _, boat ->
                    """
                    <div class="reservation-card" id="boat-${boat.id}" x-data="{ modalOpen: false }">
                        <div class="columns is-vcentered">
                            <div class="column is-narrow">
                                <h4>${t("citizenDetails.boat")} ${boat.name}</h4>
                            </div>
                            <div class="memo-edit-buttons column columns">
                                ${deleteButton(boat.reservationId != null, boat.id)}
                                
                                ${showBoatWarnings(boat.hasAnyWarnings() && userType == UserType.EMPLOYEE)}
                                
                                ${editBoatButton(boat)}
                            </div>
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
                                    <label class="label">${t("boatSpaceReservation.title.ownershipStatus")} 
                                        ${if (isEmployee &&
                        (
                            boat.hasWarning(
                                ReservationWarningType.BoatFutureOwner.name
                            ) ||
                                boat.hasWarning(ReservationWarningType.BoatCoOwner.name)
                        )
                    ) {
                        """<span class="icon ml-s">${icons.warningExclamation(false)}</span>"""
                    } else {
                        ""
                    }}
                                    </label>
                                    <p id="boat-ownership-text-${boat.id}">${t(
                        "boatApplication.$userType.ownershipOption.${boat.ownership}"
                    )}</p>
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

        val boatsWithNoReservation = getBoatsList(boats.filter { it.reservationId == null }, userType == UserType.EMPLOYEE)

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
                       ${if (userType == UserType.EMPLOYEE) renderTabNavi(citizen.id, SubTab.Reservations) else ""}
                       <h3>${t("boatSpaceReservation.title.splitReservations")}</h3>
                        $reservationList
                       <h3>${t("boatSpaceReservation.title.boats")}</h3>
                       <div class="reservation-list form-section no-bottom-border">
                           ${getBoatsList(boats.filter { it.reservationId != null }, userType == UserType.EMPLOYEE)} 
                       </div>
                     
                      <div>
                         $showAllBoatsCheckbox
                          <div class="reservation-list form-section" x-show="showAllBoats">    
                            ${getBoatsList(boats.filter { it.reservationId == null }, userType == UserType.EMPLOYEE)} 
                           </div>
                      </div>
                      <div 
                          hx-get="/reservation/partial/expired-boat-space-reservation-list/${citizen.id}" 
                          hx-trigger="load"
                          hx-swap="outerHTML"
                          ${addTestId("expired-reservation-list-loader")}
                          class='mt-1'>  
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
        messages: List<QueuedMessage>,
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

    fun formatDate(d: LocalDateTime): String = d.format(fullDateTimeFormat)

    fun memoContent(
        memo: ReserverMemoWithDetails,
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
                   hx-get="${getTabUrl("${memo.reserverId}/muistiinpanot/muokkaa/${memo.id}")}"
                   hx-trigger="click"
                   hx-target="#memo-${memo.id}"
                   hx-swap="outerHTML">
                    <span class="icon ml-m">
                        ${icons.edit}
                    </span>
                </a>
                <a id="delete-memo-button"
                   hx-delete="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
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
                <form hx-patch="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
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
                                  hx-get="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
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
        memos: List<ReserverMemoWithDetails>,
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
