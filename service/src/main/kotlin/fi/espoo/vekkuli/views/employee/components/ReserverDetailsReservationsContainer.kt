package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.config.ReservationWarningType
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.getReference
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.citizen.details.reservation.ReservationList
import fi.espoo.vekkuli.views.components.WarningBox
import fi.espoo.vekkuli.views.employee.SanitizeInput
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.isNotEmpty

@Component
class ReserverDetailsReservationsContainer(
    private val reservationListBuilder: ReservationList,
    private val warningBox: WarningBox,
) : BaseView() {
    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    fun render(
        reserver: ReserverWithDetails,
        @SanitizeInput boatSpaceReservations: List<BoatSpaceReservationDetails>,
        @SanitizeInput boats: List<CitizenUserController.BoatUpdateForm>,
        userType: UserType,
        reserverType: ReserverType,
    ): String {
        val reserverId = reserver.id
        val reservationList = reservationListBuilder.render(boatSpaceReservations, userType, reserverId)

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
                        <label class="checkbox pb-s">
                            <input type="checkbox" name="key" value="$warning">
                            <span>${t("reservationWarning.$warning")}</span>
                        </label>
                        """.trimIndent()
                    }
                return """
                    <div class="modal" x-show="modalOpen" style="display:none;">
                        <div class="modal-underlay" @click="modalOpen = false"></div>
                        <div class="modal-content">
                            <form hx-post="/virkailija/venepaikat/varaukset/kuittaa-varoitus"
                                  hx-swap="outerHTML"
                                  hx-target="#reserver-details"
                                 >
                                <input type="hidden" name="boatId" value="${boat.id}" />
                                <input type="hidden" name="reserverId" value="$reserverId" />
                                <div class="block">
                                    <div class="field">
                                        <h1 class="label">${t("citizenDetails.warnings.ackSelect")}</h1>
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
                return "/virkailija/${reserverType.toPath()}/$reserverId/vene/$boatId/poista"
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
                                                hx-select="#reserver-details"
                                                hx-target="#reserver-details">
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
                "/virkailija/kayttaja/$reserverId/vene/$boatId/muokkaa"
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

        fun boatInfoLabel(
            translationKey: String,
            showWarning: Boolean
        ): String {
            val warning =
                if (showWarning) {
                    """<span class="icon ml-s">${icons.warningExclamation(false)}</span>"""
                } else {
                    ""
                }
            return """
                    <label class="label">${t(translationKey)}
                $warning
                    </label> 
                """.trimIndent()
        }

        fun boatInfo(
            id: String,
            value: String,
            translationKey: String,
            showWarning: Boolean = false
        ): String =
            """
            <div class="field">
                ${boatInfoLabel(translationKey, showWarning)}
                <p id="$id">$value</p>
            </div> 
            """.trimIndent()

        fun getBoatsList(
            boats: List<CitizenUserController.BoatUpdateForm>,
            showWarnings: Boolean,
        ): String =
            boats
                .mapIndexed { _, boat ->
                    val name =
                        boatInfo(
                            "boat-name-text-${boat.id}",
                            if (!boat.name.isEmpty()) boat.name else "-",
                            "boatSpaceReservation.title.boatName"
                        )
                    val weight =
                        boatInfo(
                            "boat-weight-text-${boat.id}",
                            boat.weight.toString(),
                            "boatSpaceReservation.title.weight",
                            showWarnings && boat.hasWarning(ReservationWarningType.BoatWeight)
                        )
                    val boatType =
                        boatInfo(
                            "boat-type-text-${boat.id}",
                            t("boatApplication.boatTypeOption.${boat.type.name}"),
                            "boatSpaceReservation.title.boatType"
                        )

                    val depth =
                        boatInfo(
                            "boat-depth-text-${boat.id}",
                            formatDecimal(boat.depth),
                            "boatSpaceReservation.title.draft",
                        )
                    val width =
                        boatInfo(
                            "boat-width-text-${boat.id}",
                            formatDecimal(boat.width),
                            "shared.label.widthInMeters",
                            showWarnings && boat.hasWarning(ReservationWarningType.BoatWidth)
                        )
                    val registrationNumber =
                        boatInfo(
                            "boat-registrationNumber-text-${boat.id}",
                            boat.registrationNumber,
                            "boatSpaceReservation.title.registrationNumber",
                            showWarnings &&
                                (boat.hasWarning(ReservationWarningType.BoatRegistrationCodeChange))
                        )
                    val length =
                        boatInfo(
                            "boat-length-text-${boat.id}",
                            formatDecimal(boat.length),
                            "shared.label.lengthInMeters",
                            showWarnings && boat.hasWarning(ReservationWarningType.BoatLength)
                        )
                    val ownershipStatus =
                        boatInfo(
                            "boat-ownership-text-${boat.id}",
                            t("boatApplication.$userType.ownershipOption.${boat.ownership}"),
                            "boatSpaceReservation.title.ownershipStatus",
                            showWarnings &&
                                (
                                    boat.hasWarning(ReservationWarningType.BoatFutureOwner) ||
                                        boat.hasWarning(ReservationWarningType.BoatCoOwner) ||
                                        boat.hasWarning(ReservationWarningType.BoatOwnershipChange)
                                )
                        )
                    val otherIdentifier =
                        boatInfo(
                            "boat-otherIdentifier-text-${boat.id}",
                            boat.otherIdentifier,
                            "boatSpaceReservation.title.otherIdentifier"
                        )
                    val additionalInfo =
                        boatInfo(
                            "boat-extraInformation-text-${boat.id}",
                            boat.extraInformation.ifEmpty { "-" },
                            "boatSpaceReservation.title.additionalInfo"
                        )
                    // language=HTML
                    """
                    <div class="reservation-card" id="boat-${boat.id}" x-data="{ modalOpen: false }">
                        <div class="columns is-vcentered">
                            <div class="column is-narrow">
                                <h4>${boat.name}</h4>
                            </div>
                            <div class="memo-edit-buttons column columns">
                                ${deleteButton(boat.reservationId != null, boat.id)}
                                ${showBoatWarnings(boat.hasAnyWarnings() && userType == UserType.EMPLOYEE)}
                                ${editBoatButton(boat)}
                            </div>
                        </div>
                        <div class="columns">
                            <div class="column">
                                $name
                                $weight
                            </div>
                            <div class="column">
                                $boatType   
                                $depth
                            </div>
                            <div class="column">
                                $width
                                $registrationNumber
                            </div>
                            <div class="column">
                                $length
                                $ownershipStatus
                            </div>
                        </div>
                         <div class="columns">
                            <div class="column is-one-quarter">
                                $otherIdentifier
                            </div>
                            <div class="column">
                                $additionalInfo
                            </div>
                        </div>
                        ${showBoatWarnings(boat)}
                    </div>
                    """.trimIndent()
                }.joinToString("\n")

        val boatsWithNoReservation =
            getBoatsList(boats.filter { it.reservationId == null }, userType == UserType.EMPLOYEE)

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
                showAllBoats: document.getElementById('showAllBoats')?.checked ?? false 
            }">
                       ${if (userType == UserType.EMPLOYEE) reserverDetailsTabs.renderTabNavi(reserver, SubTab.Reservations) else ""}
                       <h3>${t("boatSpaceReservation.title.splitReservations")}</h3>
                        $reservationList
                       
                       <div 
                          hx-get="/reservation/partial/expired-boat-space-reservation-list/$reserverId" 
                          hx-trigger="load"
                          hx-swap="outerHTML"
                          ${addTestId("expired-reservation-list-loader")}
                          class='mt-1'>  
                       </div>
                       
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
                   </div>
            """.trimIndent()
    }

    fun paymentTabContent(
        reserver: ReserverWithDetails,
        paymentHistory: List<PaymentHistory>
    ): String {
        fun createRefundButton(paymentId: UUID): String =
            """
            <div class="column" x-data="{refundModal: false}">
                <a class="is-link has-text-danger"
                    id="refund-payment-button-$paymentId"
                    data-testid="refund-payment-button"
                   x-on:click="refundModal = true">
                   <span class="button-like">${t("citizenDetails.payments.refund")}</span>
                </a>
                <div class="modal" x-show="refundModal" style="display:none;">
                    <div class="modal-underlay" @click="refundModal = false"></div>
                    <div class="modal-content">
                        <div class="container">
                            <div class="has-text-centered is-1">
                                <p class='mb-m'>${t("citizenDetails.payments.refund.long")}</p>
                                <div class="buttons is-centered">
                                    <a class="button is-secondary" id="refund-payment-modal-cancel" x-on:click="refundModal = false">
                                        ${t("cancel")}
                                    </a>
                                    <a class="button is-danger" 
                                        id="refund-modal-confirm"
                                        data-testid="refund-payment-modal-confirm" 
                                        hx-post="${"/virkailija/kayttaja/${reserver.id}/maksut/$paymentId/hyvita"}"
                                        hx-select="#reserver-details"
                                        hx-target="#reserver-details">
                                        ${t("confirm")}
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent()

        fun createAckInvoicePaymentReservationWarningButton(reservationWarningId: UUID): String =
            """
            <div class="column" x-data="{invoicePaymentRWModal: false}">
                <a class="is-link"
                    id="invoice-payment-rw-button-$reservationWarningId"
                    data-testid="invoice-payment-rw-button"
                   x-on:click="invoicePaymentRWModal = true">
                    <div class="centered-row">
                        <div>${icons.warningExclamation(false)}</div>
                        <span class="ack-link">${t("citizenDetails.payments.ackRW")}</span>
                    </div>
                </a>
                <div class="modal" x-show="invoicePaymentRWModal" style="display:none;">
                    <div class="modal-underlay" @click="invoicePaymentRWModal = false"></div>
                    <div class="modal-content invoice-payment-ack-modal">
                        <form hx-post="/virkailija/kayttaja/${reserver.id}/maksut/kuittaa/$reservationWarningId"
                              hx-swap="outerHTML"
                              hx-target="#reserver-details"
                             >                            
                            <div class="block">
                                <h2>${t("citizenDetails.payments.ackRW.title")}</h2>
                                <div class="block">
                                    <h4>${t("citizenDetails.payments.ackRW.description")}</h3>
                                    <input type="text" name="content" class="input">
                                </div>
                                <div>
                                    ${warningBox.render(t("citizenDetails.payments.ackRW.info"))}
                                </div>
                            </div>
                            
                            <div class="block">
                                <button id="ack-modal-cancel"
                                        class="button"
                                        x-on:click="invoicePaymentRWModal = false"
                                        type="button">
                                    ${t("cancel")}
                                </button>
                                <button
                                        id="ack-modal-confirm"
                                        class="button is-primary"
                                        type="submit">
                                    ${t("citizenDetails.payments.ackRW.confirm")}
                                </button>
                            </div>
                            
                        </form>
                    </div>
                </div>
            </div>
            """.trimIndent()

        val paymentHistoryRowsHtml =
            paymentHistory.joinToString("\n") { p ->
                // language=HTML
                """
                <tr>
                    <td ${addTestId("payment-status")}>${paymentStatusToText(p.paymentDetails.paymentStatus)}</td>
                    <td ${addTestId("place")}>${p.paymentDetails.harborName} ${p.paymentDetails.place}</td>
                    <td>${boatSpaceTypeToText(p.paymentDetails.boatSpaceType)}</td>
                    <td>${p.paymentDetails.paymentReference}</td>
                    <td>${paymentTypeToText(p.paymentDetails.paymentType)}</td>
                    <td ${addTestId("payment-reference")} class='description'>${getReference(p) ?: ""}</td>
                    <td>${p.paymentDetails.invoiceDueDate?.format(fullDateFormat) ?: ""}</td>
                    <td ${addTestId("payment-paid-date")}>${p.paymentDetails.paidDate?.format(fullDateFormat) ?: ""}</td>
                    <td ${addTestId("payment-amount")}>${formatInt(p.paymentDetails.totalCents)}</td>
                    <td>${p.paymentDetails.paymentCreated.format(fullDateTimeFormat)}</td>
                    <td>${if (p.paymentDetails.paymentStatus != PaymentStatus.Refunded) {
                    createRefundButton(
                        p.paymentDetails.paymentId
                    )
                } else {
                    ""
                }}</td>
                </tr>
                
                ${p.invoicePayments.joinToString("\n") { i ->
                    """
                    <tr class='settlement-row' ${addTestId("settlement-row")}>
                        <td>
                            <div class='centered-row'>
                            ${if (i.reservationWarningId != null) {
                        "<span>${
                            icons.warningExclamation(
                                false
                            )
                        }</span>"
                    } else {
                        ""
                    }}<span>${t("citizenDetails.payments.settlement")}</span>
                            </div>
                        </td>
                        <td></td>
                        <td></td>
                        <td ${addTestId("settlement-invoice-number")}>${i.invoiceNumber}</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td>${i.paymentDate.format(fullDateFormat)}</td>
                        <td>${formatInt(i.amountPaidCents)}</td>
                        <td>${if (i.reservationWarningId != null) {
                        createAckInvoicePaymentReservationWarningButton(
                            i.reservationWarningId
                        )
                    } else {
                        ""
                    }}
                        </td>
                        <td></td>
                    </tr>
                    """
                }}
                """.trimIndent()
            }

        val paymentsTableHtml =
            if (paymentHistory.isNotEmpty()) {
                // language=HTML
                """
                <div class="payment-list">
                    <table id="payments-table">
                      <thead>
                        <tr>
                          <th>${t("citizenDetails.payments.status")}</th>
                          <th>${t("citizenDetails.payments.place")}</th>
                          <th>${t("citizenDetails.payments.placeType")}</th>
                          <th>${t("citizenDetails.payments.paymentReference")}</th>
                          <th>${t("citizenDetails.payments.type")}</th>
                          <th>${t("citizenDetails.payments.invoiceReference")}</th>
                          <th>${t("citizenDetails.payments.invoiceDueDate")}</th>
                          <th>${t("citizenDetails.payments.paidDate")}</th>
                          <th>${t("citizenDetails.payments.totalCents")}</th>
                          <th>${t("citizenDetails.payments.paymentCreated")}</th>
                          <th></th>
                        </tr>
                      </thead>
                      <tbody>
                          $paymentHistoryRowsHtml
                      </tbody>
                    </table>
                </div>
                """.trimIndent()
            } else {
                "<h2 data-testid='no-payments-indicator'>${t("citizenDetails.payments.noPayments")}</h2>"
            }

        // language=HTML
        return """
            <div id="tab-content" class="container block">
              ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Payments)}
              <h3>${t("citizenDetails.payments.title")}</h3>
              $paymentsTableHtml
            </div>
            """.trimIndent()
    }
}
