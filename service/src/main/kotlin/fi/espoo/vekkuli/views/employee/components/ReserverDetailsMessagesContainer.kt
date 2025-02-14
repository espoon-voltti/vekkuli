package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.collections.isNotEmpty

@Component
class ReserverDetailsMessagesContainer : BaseView() {
    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    fun messageTabContent(
        reserver: ReserverWithDetails,
        messages: List<QueuedMessage>,
    ): String {
        val messageHtml =
            messages.joinToString("\n") { message ->
                // language=HTML
                """
                <tr 
                    hx-get="/virkailija/kayttaja/${reserver.id}/viestit/${message.id}"
                    hx-target="#modal-container"
                    hx-swap="innerHTML">
                    <td><a>${message.subject}</a></td>
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
              ${reserverDetailsTabs.renderTabNavi(reserver, SubTab.Messages)}
              $messagesHtml
            </div>
            """.trimIndent()
    }

    fun formatDate(d: LocalDateTime): String = d.format(fullDateTimeFormat)
}
