package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.TextAreaOptions
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import fi.espoo.vekkuli.views.components.modal.ModalButtonStyle
import fi.espoo.vekkuli.views.components.modal.ModalButtonType
import org.springframework.stereotype.Component

@Component
class SendMessageView(
    private var modal: Modal,
    private var formComponents: FormComponents
) : BaseView() {
    fun renderLink(totalRows: Int): String {
        //language=HTML
        return """            
            <a                     
                class="${if (totalRows == 0) "disabled" else ""} is-link has-text-weight-semibold"
                hx-target="#modal-container"
                hx-swap="innerHTML"
                hx-boost="false"
                hx-push-url="false"
                hx-include="#reservation-filter-form"
                hx-get="/virkailija/viestit/massa/modal"
                ${addTestId("send-mass-email-link")}>${t("employee.messages.title", listOf(totalRows.toString()))}
            </a>
            """.trimIndent()
    }

    fun renderSendMessageModal(
        reservationCount: Int,
        recipients: List<Recipient>
    ): String {
        val emails = recipients.joinToString("\n") { it.email }
        val modalBuilder = modal.createModalBuilder()
        val formId = "send-mass-email"
        val messageTitleField =
            formComponents.textInput(
                labelKey = "boatSpaceTermination.fields.messageTitle",
                id = "message-title",
                name = "messageTitle",
                required = true,
                value = ""
            )
        val messageContentField =
            formComponents.textArea(
                TextAreaOptions(
                    labelKey = "boatSpaceTermination.fields.messageContent",
                    id = "message-content",
                    name = "messageContent",
                    required = true,
                    rows = 14,
                    resizable = true,
                    value = ""
                )
            )
        val sendButtonStyle =
            if (recipients.size > 49) {
                ModalButtonStyle.Danger
            } else {
                ModalButtonStyle.Primary
            }

        return modalBuilder
            .setTitle(t("employee.messages.modal.title"))
            .setForm {
                setId(formId)
                setTestId(formId)
                setAttributes(
                    mapOf(
                        "hx-post" to "/virkailija/viestit/massa/laheta",
                        "hx-swap" to "innerHTML",
                        "hx-target" to "#modal-container",
                        "hx-include" to "#reservation-filter-form"
                    )
                )
            }
            //language=HTML
            .setContent(
                """
                    <script>
                        function toggleEmailList(element) {
                            let emailList = document.getElementById("recipient-emails");
                            emailList.innerText = "Vastaanottajat:\n " +element.getAttribute("data-emails");
                            
                            if (emailList.style.display === "block") {
                                emailList.style.display = "none";
                                element.innerText = "Näytä vastaanottajat";
                                return;
                            }
                            emailList.style.display = "block";
                            element.innerText = "Piilota vastaanottajat";
                        }
                       
                    </script>
                <div class='columns is-multiline'>
                    <div class="column is-full" ${addTestId("send-mass-email-modal-subtitle")}>
                            ${t("employee.messages.modal.subtitle", listOf(reservationCount.toString(), recipients.size.toString()))}
                    </div>
                    <div class="column is-full recipients"><a data-emails="$emails" onclick="toggleEmailList(this)">Näytä vastaanottajat</a></div>
                    <div id="recipient-emails"></div>
                    <div class="column is-full">
                        $messageTitleField                        
                        $messageContentField
                    </div>
                 </div>                 
                """.trimIndent()
            ).addButton {
                setText(t("cancel"))
                setType(ModalButtonType.Cancel)
                setTestId("send-mass-email-modal-cancel")
            }.addButton {
                setStyle(sendButtonStyle)
                setType(ModalButtonType.Submit)
                setText(t("employee.messages.modal.send.title", listOf(recipients.size.toString())))
                setTestId("send-mass-email-modal-confirm")
            }.build()
    }

    fun renderMessageSentFeedback(recipientCount: Int): String {
        val modalBuilder = modal.createModalBuilder()
        val stateId = modalBuilder.getModalStateId()
        val closeModalInMs = 3000
        return modalBuilder
            .setReloadPageOnClose(true)
            // language=HTML
            .setContent(
                """
                <div 
                    class="columns pv-l is-multiline is-3" x-init="setTimeout(() => $stateId = false, $closeModalInMs)"
                    ${addTestId("message-sent-success-modal")}
                >
                    <div class="column is-full has-text-centered">
                        ${icons.success}
                    </div>
                    <div class="column is-full is-center">
                        <h2 class="has-text-centered mb-none">${
                    t(
                        "employee.messages.modal.success",
                        listOf(recipientCount.toString())
                    )
                }</h2>
                    </div>
                </div> 
                """.trimIndent()
            ).build()
    }

    fun renderSendingFailed(): String {
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            // language=HTML
            .setContent(
                """
                 <div class="columns is-multiline is-2" ${addTestId("message-sent-fail-modal")}>
                    <div class="column is-full has-text-centered">
                        ${icons.errorNotification}
                    </div>
                    <div class="column is-full">
                        <h2 class="has-text-centered mb-none">${t("employee.messages.modal.fail")}</h2>
                    </div>
                </div>
                """.trimIndent()
            ).addButton {
                setText(t("button.ok"))
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Danger)
                setTestId("employee-messages-modal-fail-ok")
            }.setButtonsCentered(true)
            .build()
    }
}
