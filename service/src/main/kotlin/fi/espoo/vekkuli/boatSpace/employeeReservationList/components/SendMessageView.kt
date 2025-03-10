package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.TextAreaOptions
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
                hx-get="/virkailija/viestit/massa/modal">${t("employee.messages.title", listOf(totalRows.toString()))}
            </a>
            """.trimIndent()
    }

    fun renderSendMessageModal(
        reservationCount: Int,
        recipientCount: Int
    ): String {
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
                    rows = 16,
                    resizable = true,
                    value = ""
                )
            )
        // todo: spinner to send button, amount of recipients

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
                <div class='columns is-multiline'>
                    <div class="column is-full">
                        Varauksia $reservationCount kpl, viestin vastaanottajia $recipientCount kpl.
                    </div>
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
                setStyle(ModalButtonStyle.Primary)
                setType(ModalButtonType.Submit)
                setText(t("employee.messages.modal.send.title"))
                setTestId("send-mass-email-modal-confirm")
            }.build()
    }

    fun renderMessageSentFeedback(): String {
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
                        <h2 class="has-text-centered mb-none">${t("employee.messages.modal.success")}</h2>
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
