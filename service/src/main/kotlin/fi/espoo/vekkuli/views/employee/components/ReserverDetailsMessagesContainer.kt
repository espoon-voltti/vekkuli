package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.SendMessageView
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.*
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.SubTab
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.collections.isNotEmpty

@Component
class ReserverDetailsMessagesContainer(
    private val sendMessageView: SendMessageView
) : BaseView() {
    @Autowired
    lateinit var reserverDetailsTabs: ReserverDetailsTabs

    fun messageTabContent(
        reserver: ReserverWithDetails,
        messagesWithAttachments: List<MessageWithAttachments>,
    ): String {
        val messageHtml =
            messagesWithAttachments.joinToString("\n") { messageWithAttachments ->
                // language=HTML
                """
                <tr hx-get="/virkailija/kayttaja/${reserver.id}/viestit/${messageWithAttachments.message.id}"
                    hx-target="#modal-container"
                    hx-swap="innerHTML" class='is-with-pointer'>
                    <td>${messageWithAttachments.message.subject}</td>
                    <td>${messageWithAttachments.message.recipientAddress}</td>
                    <td>${messageWithAttachments.message.sentAt?.let { formatDate(it) } ?: "Ei lähetetty"}</td>
                    <td>${messageWithAttachments.message.senderAddress ?: ""}</td>
                </tr>
                """.trimIndent()
            }

        val messagesHtml =
            if (messagesWithAttachments.isNotEmpty()) {
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
              <div id="send-message-to-reserver">
                ${sendMessageView.renderSendMessageToReserverLink(reserver.id)}
              </div>
              $messagesHtml
            </div>
            """.trimIndent()
    }

    fun formatDate(d: LocalDateTime): String = d.format(fullDateTimeFormat)
}
