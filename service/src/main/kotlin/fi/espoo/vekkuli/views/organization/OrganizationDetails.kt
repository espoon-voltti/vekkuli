package fi.espoo.vekkuli.views.organization

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.employee.SanitizeInput
import fi.espoo.vekkuli.views.organization.components.OrganizationContactDetails
import org.springframework.stereotype.Service

@Service
class OrganizationDetails(
    var commonComponents: CommonComponents,
    var formComponents: FormComponents,
    var icons: Icons,
    private val organizationContactDetails: OrganizationContactDetails
) : BaseView() {
    fun organizationPageForEmployee(
        @SanitizeInput organization: Organization,
        @SanitizeInput errors: MutableMap<String, String>? = mutableMapOf(),
    ): String {
        // language=HTML

        val backUrl = "/virkailija/venepaikat/varaukset"

        val result =
            // language=HTML
            """
            <section class="section" id="organization-details">
                <div class="container block">
                    ${commonComponents.goBackButton(backUrl)} 
                    <h2 class='mb-none'>${organization.name}</h2>
                </div>
                <div class='container'>
                    <h3>${t("organizationDetails.title.organizationInformation")}</h3>
                
                    <div class='form-section'>
                        ${organizationContactDetails.render(organization)}
                   </div>
               </div>
            </section>
            """.trimIndent()

        return result
    }

//    private fun getTabUrl(last: String): String = getServiceUrl("/virkailija/kayttaja/$last")
//
//    fun reservationTabContent(
//        @SanitizeInput organization: Organization,
//        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
//        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
//        userType: UserType
//    ): String {
//        val reservationList = reservationListBuilder.render(organization, boatSpaceReservations, userType, organization.id)
//
//        fun showBoatWarnings(boatHasWarnings: Boolean): String {
//            if (boatHasWarnings) {
//                // language=HTML
//                return """
//                <div class="column">
//                    <a class="is-link is-icon-link has-text-warning has-text-weight-semibold" x-on:click="modalOpen = true">
//                        <span class="icon ml-s">
//                            ${icons.warningExclamation(false)}
//                        </span>
//                        <span data-testid='acknowledge-warnings'>${t("organizationDetails.button.acknowledgeWarnings")}</span>
//                    </a>
//                </div>
//                """
//            }
//            return ""
//        }
//
//        fun showBoatWarnings(boat: organizationUserController.BoatUpdateForm): String {
//            // language=HTML
//
//            if (boat.hasAnyWarnings()) {
//                val warningLabels =
//                    boat.warnings.joinToString("\n") { warning ->
//                        """
//                        <label class="radio">
//                            <input type="radio" name="key" value="$warning">
//                            ${t("reservationWarning.$warning")}
//                        </label>
//                        """.trimIndent()
//                    }
//                return """
//                    <div class="modal" x-show="modalOpen" style="display:none;">
//                        <div class="modal-underlay" @click="modalOpen = false"></div>
//                        <div class="modal-content">
//                            <form hx-post="/virkailija/venepaikat/varaukset/kuittaa-varoitus"
//                                  hx-swap="outerHTML"
//                                  hx-target="#organization-details"
//                                 >
//                                <input type="hidden" name="boatId" value="${boat.id}" />
//                                <input type="hidden" name="organizationId" value="${organization.id}" />
//                                <input type="hidden" name="reservationId" value="${boat.reservationId}" />
//                                <div class="block">
//                                    <div class="field">
//                                        <h1 class="label">${t("organizationDetails.warnings.ackSelect")}</h1>
//                                        <div class="control">
//                                            $warningLabels
//                                        </div>
//                                    </div>
//                                </div>
//                                <div class="block">
//                                    <h1 class="label">${t("organizationDetails.label.warningInfo")}</h1>
//                                    <div class="control">
//                                        <textarea data-testid="warning-info-input" class="textarea" rows="1" name="infoText"></textarea>
//                                    </div>
//                                </div>
//                                ${warningBox.render(t("reservationWarning.ackInfo"))}
//                                <div class="block">
//                                    <button id="ack-modal-cancel"
//                                            class="button"
//                                            x-on:click="modalOpen = false"
//                                            type="button">
//                                        ${t("cancel")}
//                                    </button>
//                                    <button
//                                            id="ack-modal-confirm"
//                                            class="button is-primary"
//                                            type="submit">
//                                        ${t("confirm")}
//                                    </button>
//                                </div>
//                            </form>
//                        </div>
//                    </div>
//                    """.trimIndent()
//            }
//            return ""
//        }
//
//        fun getDeleteUrl(boatId: Int): String {
//            if (userType == UserType.EMPLOYEE) {
//                return "/virkailija/kayttaja/${organization.id}/vene/$boatId/poista"
//            }
//            return "/kuntalainen/vene/$boatId/poista"
//        }
//
//        fun deleteButton(
//            hasLinkedReservation: Boolean,
//            boatId: Int,
//        ): String {
//            if (!hasLinkedReservation) {
//                return (
//                    """
//                    <div class="column" x-data="{deleteModal: false}">
//                        <a class="is-link has-text-danger"
//                            id='delete-boat-$boatId'
//                           x-on:click="deleteModal = true">
//                            <span class="icon ml-s">
//                                ${icons.remove}
//                            </span>
//                            <span > ${t("organizationDetails.button.deleteBoat")} </span>
//                        </a>
//                        <div class="modal" x-show="deleteModal" style="display:none;">
//                            <div class="modal-underlay" @click="deleteModal = false"></div>
//                            <div class="modal-content">
//                                <div class="container">
//                                    <div class="has-text-centered is-1">
//                                        <p class='mb-m'>${t("organizationDetails.text.deleteBoatConfirmation")}</p>
//                                        <div class="buttons is-centered">
//                                            <a class="button is-secondary" id="delete-modal-cancel-$boatId" x-on:click="deleteModal = false">
//                                                ${t("cancel")}
//                                            </a>
//                                            <a class="button is-danger"
//                                                id="delete-modal-confirm-$boatId"
//                                                hx-delete="${getDeleteUrl(boatId)}"
//                                                hx-select="#organization-details"
//                                                hx-target="#organization-details">
//                                                ${t("organizationDetails.button.confirmDeletion")}
//                                            </a>
//                                        </div>
//                                    </div>
//                                </div>
//                            </div>
//                        </div>
//                    </div>
//                    """
//                )
//            }
//            return ""
//        }
//
//        val getEditUrl = { boatId: Int ->
//            if (userType == UserType.EMPLOYEE) {
//                "/virkailija/kayttaja/${organization.id}/vene/$boatId/muokkaa"
//            } else {
//                "/kuntalainen/vene/$boatId/muokkaa"
//            }
//        }
//
//        fun editBoatButton(boat: CitizenUserController.BoatUpdateForm): String {
//            // language=HTML
//            return """
//                <div class="column is-narrow ml-auto">
//                    <a class="is-icon-link is-link"
//                       hx-get="${getEditUrl(boat.id)}"
//                       hx-target="#boat-${boat.id}"
//                       hx-swap="innerHTML">
//                        <span class="icon">
//                            ${icons.edit}
//                        </span>
//                        <span id="edit-boat-${boat.id}"> ${t("organizationDetails.button.editBoatDetails")}</span>
//                    </a>
//                </div>
//                """.trimIndent()
//        }
//
//        fun boatInfoLabel(
//            translationKey: String,
//            showWarning: Boolean
//        ): String {
//            val warning =
//                if (showWarning) {
//                    """<span class="icon ml-s">${icons.warningExclamation(false)}</span>"""
//                } else {
//                    ""
//                }
//            return """
//                    <label class="label">${t(translationKey)}
//                $warning
//                    </label>
//                """.trimIndent()
//        }
//
//        fun boatInfo(
//            id: String,
//            value: String,
//            translationKey: String,
//            showWarning: Boolean = false
//        ): String =
//            """
//            <div class="field">
//                ${boatInfoLabel(translationKey, showWarning)}
//                <p id="$id">$value</p>
//            </div>
//            """.trimIndent()
//
//        fun getBoatsList(
//            boats: List<CitizenUserController.BoatUpdateForm>,
//            showWarnings: Boolean
//        ): String =
//            boats
//                .mapIndexed { _, boat ->
//                    val name = boatInfo("boat-name-text-${boat.id}", boat.name, "organizationDetails.title.boatName")
//                    val weight =
//                        boatInfo(
//                            "boat-weight-text-${boat.id}",
//                            boat.weight.toString(),
//                            "organizationDetails.title.weight",
//                            showWarnings && boat.hasWarning(ReservationWarningType.BoatWeight.name)
//                        )
//                    val boatType =
//                        boatInfo(
//                            "boat-type-text-${boat.id}",
//                            boat.type.name,
//                            "organizationDetails.title.boatType"
//                        )
//
//                    val depth = boatInfo("boat-depth-text-${boat.id}", boat.depth.toString(), "organizationDetails.title.draft",)
//                    val width =
//                        boatInfo(
//                            "boat-width-text-${boat.id}",
//                            boat.width.toString(),
//                            "shared.label.widthInMeters",
//                            showWarnings && boat.hasWarning(ReservationWarningType.BoatWidth.name)
//                        )
//                    val registrationNumber =
//                        boatInfo(
//                            "boat-registrationNumber-text-${boat.id}",
//                            boat.registrationNumber,
//                            "organizationDetails.title.registrationNumber"
//                        )
//                    val length =
//                        boatInfo(
//                            "boat-length-text-${boat.id}",
//                            boat.length.toString(),
//                            "shared.label.lengthInMeters",
//                            showWarnings && boat.hasWarning(ReservationWarningType.BoatLength.name)
//                        )
//                    val ownershipStatus =
//                        boatInfo(
//                            "boat-ownership-text-${boat.id}",
//                            t("boatApplication.$userType.ownershipOption.${boat.ownership}"),
//                            "organizationDetails.title.ownershipStatus",
//                            showWarnings &&
//                                (
//                                    boat.hasWarning(ReservationWarningType.BoatFutureOwner.name) ||
//                                        boat.hasWarning(ReservationWarningType.BoatCoOwner.name)
//                                )
//                        )
//                    val otherIdentifier =
//                        boatInfo(
//                            "boat-otherIdentifier-text-${boat.id}",
//                            boat.otherIdentifier,
//                            "organizationDetails.title.otherIdentifier"
//                        )
//                    val additionalInfo =
//                        boatInfo(
//                            "boat-extraInformation-text-${boat.id}",
//                            boat.extraInformation.ifEmpty { "-" },
//                            "organizationDetails.title.additionalInfo"
//                        )
//                    // language=HTML
//                    """
//                    <div class="reservation-card" id="boat-${boat.id}" x-data="{ modalOpen: false }">
//                        <div class="columns is-vcentered">
//                            <div class="column is-narrow">
//                                <h4>${t("organizationDetails.boat")} ${boat.name}</h4>
//                            </div>
//                            <div class="memo-edit-buttons column columns">
//                                ${deleteButton(boat.reservationId != null, boat.id)}
//                                ${showBoatWarnings(boat.hasAnyWarnings() && userType == UserType.EMPLOYEE)}
//                                ${editBoatButton(boat)}
//                            </div>
//                        </div>
//                        <div class="columns">
//                            <div class="column">
//                                $name
//                                $weight
//                            </div>
//                            <div class="column">
//                                $boatType
//                                $depth
//                            </div>
//                            <div class="column">
//                                $width
//                                $registrationNumber
//                            </div>
//                            <div class="column">
//                                $length
//                                $ownershipStatus
//                            </div>
//                        </div>
//                         <div class="columns">
//                            <div class="column is-one-quarter">
//                                $otherIdentifier
//                            </div>
//                            <div class="column">
//                                $additionalInfo
//                            </div>
//                        </div>
//                        ${showBoatWarnings(boat)}
//                    </div>
//                    """.trimIndent()
//                }.joinToString("\n")
//
//        val boatsWithNoReservation = getBoatsList(boats.filter { it.reservationId == null }, userType == UserType.EMPLOYEE)
//
//        // language=HTML
//        val showAllBoatsCheckbox =
//            if (boatsWithNoReservation.isNotEmpty()) {
//                """
//                            <label class="checkbox pb-l">
//                                <input type="checkbox"
//                                name="showAllBoats"
//                                id="showAllBoats"
//                                x-model="showAllBoats"
//                                hx-preserve="true"
//                                x-ref="showAllBoats"
//                                />
//                                <span>${t("organizationDetails.checkbox.showAllBoats")}</span>
//                            </label>
//                """
//            } else {
//                ""
//            }
//
//        // language=HTML
//        return """
//                   <div id="tab-content" class="container block" x-data="{
//                showAllBoats: document.getElementById('showAllBoats').checked
//            }">
//                       ${if (userType == UserType.EMPLOYEE) renderTabNavi(organization.id, SubTab.Reservations) else ""}
//                       <h3>${t("organizationDetails.title.splitReservations")}</h3>
//                        $reservationList
//                       <h3>${t("organizationDetails.title.boats")}</h3>
//                       <div class="reservation-list form-section no-bottom-border">
//                           ${getBoatsList(boats.filter { it.reservationId != null }, userType == UserType.EMPLOYEE)}
//                       </div>
//
//                      <div>
//                         $showAllBoatsCheckbox
//                          <div class="reservation-list form-section" x-show="showAllBoats">
//                            ${getBoatsList(boats.filter { it.reservationId == null }, userType == UserType.EMPLOYEE)}
//                           </div>
//                      </div>
//                      <div
//                          hx-get="/reservation/partial/expired-boat-space-reservation-list/${organization.id}"
//                          hx-trigger="load"
//                          hx-swap="outerHTML"
//                          ${addTestId("expired-reservation-list-loader")}
//                          class='mt-1'>
//                       </div>
//                   </div>
//            """.trimIndent()
//    }
//
//    fun paymentTabContent(organization: Organization): String {
//        // language=HTML
//        return """
//            <div id="tab-content" class="container block">
//              ${renderTabNavi(organization.id, SubTab.Payments)}
//              <h3>PAYMENTS</h3>
//            </div>
//            """.trimIndent()
//    }
//
//    fun messageTabContent(
//        organization: Organization,
//        messages: List<QueuedMessage>,
//    ): String {
//        val messageHtml =
//            messages.joinToString("\n") { message ->
//                // language=HTML
//                """
//                <tr>
//                    <td>${message.subject}</td>
//                    <td>${message.recipientAddress}</td>
//                    <td>${message.sentAt?.let { formatDate(it) } ?: "Ei lähetetty"}</td>
//                    <td>${message.senderAddress ?: ""}</td>
//                </tr>
//                """.trimIndent()
//            }
//
//        val messagesHtml =
//            if (messages.isNotEmpty()) {
//                // language=HTML
//                """
//                <div class="message-list">
//                    <table id="messages-table">
//                      <thead>
//                        <tr>
//                          <th>${t("organizationDetails.messages.subject")}</th>
//                          <th>${t("organizationDetails.messages.recipient")}</th>
//                          <th>${t("organizationDetails.messages.sentAt")}</th>
//                          <th>${t("organizationDetails.messages.sender")}</th>
//                        </tr>
//                      </thead>
//                      <tbody>
//                          $messageHtml
//                      </tbody>
//                    </table>
//                </div>
//                """.trimIndent()
//            } else {
//                "<h2>${t("organizationDetails.messages.noMessages")}</h2>"
//            }
//
//        // language=HTML
//        return """
//            <div id="tab-content" class="container block">
//              ${renderTabNavi(organization.id, SubTab.Messages)}
//              $messagesHtml
//            </div>
//            """.trimIndent()
//    }
//
//    fun formatDate(d: LocalDateTime): String = d.format(fullDateTimeFormat)
//
//    fun memoContent(
//        memo: ReserverMemoWithDetails,
//        edit: Boolean,
//    ): String {
//        val createdBy =
//            if (memo.createdBy !== null) {
//                """
//                <span class="memo-label">${memo.createdBy}</span>
//                """
//            } else {
//                ""
//            }
//
//        val buttons =
//            if (edit) {
//                ""
//            } else {
//                """
//                <a id="edit-memo-button"
//                   hx-get="${getTabUrl("${memo.reserverId}/muistiinpanot/muokkaa/${memo.id}")}"
//                   hx-trigger="click"
//                   hx-target="#memo-${memo.id}"
//                   hx-swap="outerHTML">
//                    <span class="icon ml-m">
//                        ${icons.edit}
//                    </span>
//                </a>
//                <a id="delete-memo-button"
//                   hx-delete="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
//                   hx-trigger="click"
//                   hx-target="#tab-content"
//                   hx-swap="outerHTML"
//                   hx-confirm="${t("organizationDetails.removeMemoConfirm")}">
//                    <span class="icon ml-m">
//                        ${icons.remove}
//                    </span>
//                </a>
//                """.trimIndent()
//            }
//
//        val header =
//            """
//            <div>
//                <span class="memo-label">${formatDate(memo.createdAt)}</span>
//                $createdBy
//                $buttons
//            </div>
//            """.trimIndent()
//
//        val updated =
//            if (memo.updatedBy !== null && memo.updatedAt !== null) {
//                """
//                <div class="memo-updated-by">
//                    Muokattu
//                    <span>${formatDate(memo.updatedAt)}</span>
//                    <span>${memo.updatedBy}</span>
//                </div>
//                """.trimIndent()
//            } else {
//                ""
//            }
//
//        val content =
//            if (edit) {
//                // language=HTML
//                """
//                <form hx-patch="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
//                      hx-target="#memo-${memo.id}"
//                      hx-swap="outerHTML">
//                    <div class="control memo-edit-area">
//                        <textarea id="edit-memo-content"
//                                  class="textarea"
//                                  rows="1"
//                                  class="memo-content-input"
//                                  name="content">${memo.content}</textarea>
//                        <div class="memo-edit-buttons">
//                            <button id="save-edit-button" type="submit">
//                                <span class="icon ml-m"
//                                      style='stroke: green;'>
//                                    ${icons.check}
//                                </span>
//                            </button>
//                            <span id="cancel-edit-button" class="icon ml-m"
//                                  hx-get="${getTabUrl("${memo.reserverId}/muistiinpanot/${memo.id}")}"
//                                  hx-trigger="click"
//                                  hx-target="#memo-${memo.id}"
//                                  hx-swap="outerHTML">
//                                ${icons.xMark}
//                            </span>
//                        </div>
//                    </div>
//                </form>
//                """.trimIndent()
//            } else {
//                // language=HTML
//                """
//                <div class="memo-content">
//                    ${memo.content}
//                </div>
//                """
//            }
//
//        // language=HTML
//        return """
//            <div class="block memo" id="memo-${memo.id}">
//               $header
//               $updated
//               $content
//            </div>
//            """.trimIndent()
//    }
//
//    fun newMemoContent(
//        organizationId: UUID,
//        edit: Boolean,
//    ): String {
//        if (edit) {
//            // language=HTML
//            return """
//                <div id="new-memo" class="block">
//                    <form hx-post="${getTabUrl("$organizationId/muistiinpanot")}"
//                        hx-target="#tab-content"
//                        hx-swap="outerHTML">
//                        <div class="memo-edit-area">
//                            <div class="control">
//                                <textarea id="new-memo-content"
//                                          class="textarea"
//                                          rows="1"
//                                          class="memo-content-input"
//                                          name="content"></textarea>
//                            </div>
//                            <div class="memo-edit-buttons">
//                                <button id="new-memo-save-button" type="submit">
//                                    <span class="icon ml-m" style='stroke: green;'>${icons.check}</span>
//                                </button>
//                                <a id="new-memo-cancel-button"
//                                   hx-get="${getTabUrl("$organizationId/muistiinpanot/lisaa_peruuta")}"
//                                    hx-trigger="click"
//                                    hx-target="#new-memo"
//                                    hx-swap="outerHTML" >
//                                    <span class="icon ml-m">${icons.xMark}</span>
//                                </a>
//                            </div>
//                        </div>
//                    </form>
//                </div>
//                """.trimIndent()
//        }
//        // language=HTML
//        return """
//            <div id="new-memo" class="block">
//                <a
//                    id="add-new-memo"
//                    hx-get="${getTabUrl("$organizationId/muistiinpanot/lisaa")}"
//                    hx-trigger="click"
//                    hx-target="#new-memo"
//                    hx-swap="outerHTML">
//                    <span class="icon mr-s">${icons.plus}</span>
//                    <span>${t("organizationDetails.newMemo")}</span>
//                </a>
//            </div>
//            """.trimIndent()
//    }
//
//    fun memoTabContent(
//        organizationId: UUID,
//        memos: List<ReserverMemoWithDetails>,
//    ): String {
//        val memoHtml =
//            memos.joinToString("\n") {
//                memoContent(it, false)
//            }
//
//        val result =
//            // language=HTML
//            """
//            <div id="tab-content" class="container block">
//                ${renderTabNavi(organizationId, SubTab.Memos)}
//                ${newMemoContent(organizationId, false)}
//                $memoHtml
//            <div>
//            """.trimIndent()
//
//        return result
//    }
//
//    fun tabCls(
//        activeTab: SubTab,
//        tab: SubTab,
//    ): String {
//        if (activeTab == tab) return "is-active"
//        return ""
//    }
//
//    fun renderTabNavi(
//        organizationId: UUID,
//        activeTab: SubTab,
//    ): String =
//        // language=HTML
//        """
//        <div class="tabs is-boxed secondary-tabs">
//            <ul>
//                <li class="${tabCls(activeTab, SubTab.Reservations)}">
//                    <a id="reservations-tab-navi"
//                       hx-get="${getTabUrl("$organizationId/varaukset")}"
//                       hx-target="#tab-content"
//                       hx-trigger="click"
//                       hx-swap="outerHTML">${t("organizationDetails.title.reservations")}</a>
//                </li>
//                <li class="${tabCls(activeTab, SubTab.Messages)}">
//                    <a id="messages-tab-navi"
//                       hx-get="${getTabUrl("$organizationId/viestit")}"
//                       hx-target="#tab-content"
//                       hx-trigger="click"
//                       hx-swap="outerHTML"> ${t("organizationDetails.title.messages")}</a>
//                </li>
//                <li class="${tabCls(activeTab, SubTab.Payments)}">
//                    <a id="payments-tab-navi"
//                       hx-get="${getTabUrl("$organizationId/maksut")}"
//                       hx-target="#tab-content"
//                       hx-trigger="click"
//                       hx-swap="outerHTML">${t("organizationDetails.title.paymentHistory")}</a>
//                </li>
//               <li class="${tabCls(activeTab, SubTab.Memos)}">
//                    <a id="memos-tab-navi"
//                       hx-get="${getTabUrl("$organizationId/muistiinpanot")}"
//                       hx-target="#tab-content"
//                       hx-trigger="click"
//                       hx-swap="outerHTML">${t("organizationDetails.title.notes")}</a>
//               </li>
//            </ul>
//        </div>
//        """.trimIndent()
}
