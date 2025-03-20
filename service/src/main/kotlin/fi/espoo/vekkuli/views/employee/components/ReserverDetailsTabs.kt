package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.ReservationWarningType
import fi.espoo.vekkuli.controllers.Utils.Companion.getServiceUrl
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.ReservationWarningRepository
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ReserverDetailsTabs : BaseView() {
    @Autowired
    lateinit var warningRepository: ReservationWarningRepository

    fun getTabUrl(last: String): String = getServiceUrl("/virkailija/kayttaja/$last")

    fun tabCls(
        activeTab: SubTab,
        tab: SubTab,
    ): String {
        if (activeTab == tab) return "is-active"
        return ""
    }

    fun renderTabNavi(
        reserver: ReserverWithDetails,
        activeTab: SubTab,
    ): String {
        val reserverId = reserver.id
        val attentionClass = "attention${if (reserver.hasExceptions()) " on" else ""}"

        val hasPaymentWarnings =
            warningRepository.getWarningsForReserver(reserver.id).any {
                it.key ==
                    ReservationWarningType.InvoicePayment
            }
        val paymentWarningClass = "warning-attention${if (hasPaymentWarnings) " on" else ""}"

        // language=HTML
        return """
                                                      <div class="tabs is-boxed secondary-tabs">
                                                          <ul>
                                                              <li class="${tabCls(activeTab, SubTab.Reservations)}">
                                                                  <a id="reservations-tab-navi" ${addTestId("reservations-tab-navi")}
                                                                      hx-get="${getTabUrl(" $reserverId/varaukset")}"
                                                                      hx-target="#tab-content"
                                                                      hx-trigger="click"
                                                                      hx-swap="outerHTML">${t("boatSpaceReservation.title.reservations")}
                                                                  </a>
                                                              </li>
                                                              <li class="${tabCls(activeTab, SubTab.Messages)}">
                                                                  <a id="messages-tab-navi" ${addTestId("messages-tab-navi")}
                                                                      hx-get="${getTabUrl(" $reserverId/viestit")}"
                                                                      hx-target="#tab-content"
                                                                      hx-trigger="click"
                                                                      hx-swap="outerHTML">${t("boatSpaceReservation.title.messages")}
                                                                  </a>
                                                              </li>
                                                              <li class="${tabCls(activeTab, SubTab.Payments)}">
                                                                  <a id="payments-tab-navi" ${addTestId("payments-tab-navi")}
                                                                      hx-get="${getTabUrl(" $reserverId/maksut")}"
                                                                      hx-target="#tab-content"
                                                                      hx-trigger="click"
                                                                      hx-swap="outerHTML">${t("boatSpaceReservation.title.payments")}
                                                                      <span class="$paymentWarningClass"></span>                                
                                                                  </a>
                                                              </li>
                                                              <li class="${tabCls(activeTab, SubTab.Memos)}">
                                                                  <a id="memos-tab-navi" ${addTestId("memos-tab-navi")}
                                                                      hx-get="${getTabUrl(" $reserverId/muistiinpanot")}"
                                                                      hx-target="#tab-content"
                                                                      hx-trigger="click"
                                                                      hx-swap="outerHTML">${t("boatSpaceReservation.title.notes")}
                                                                  </a>
                                                              </li>
                                                              <li class="${tabCls(activeTab, SubTab.Exceptions)}">
                                                                  <a id="exceptions-tab-navi" ${addTestId("exceptions-tab-navi")}
                                                                      hx-get="${getTabUrl(" $reserverId/poikkeukset")}"
                                                                      hx-target="#tab-content"
                                                                      hx-trigger="click"
                                                                      hx-swap="outerHTML">${t("boatSpaceReservation.title.exceptions")}
            <span class="$attentionClass" ${addTestId("exceptions-tab-attention")}></span>                                
                                                                  </a>
                                                              </li>                    
                                                          </ul>
                                                      </div>
            """.trimIndent()
    }
}
