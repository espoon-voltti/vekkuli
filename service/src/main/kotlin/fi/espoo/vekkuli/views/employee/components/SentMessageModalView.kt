package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.domain.MessageWithAttachments
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SentMessageModalView : BaseView() {
    @Autowired
    private lateinit var attachmentView: AttachmentView

    @Autowired
    private lateinit var modal: Modal

    fun render(messageWithAttachment: MessageWithAttachments): String {
        val formId = "sent-message-form"
        val modalBuilder = modal.createModalBuilder()
        val stateId = modalBuilder.getModalStateId()
        return modalBuilder
            // language=HTML
            .setContent(
                """
                <form
                    id="$formId"
                    ${addTestId(formId)}
                    hx-swap="innerHTML"
                    hx-target="#modal-container"
                    >   
                       <div class="close-modal">
                        <span @click="$stateId = false;" class="icon">${icons.xMark}</span>
                    </div>
                        <div class="meta-content">            
                            <div class="columns">
                                <div class="column is-two-fifths">
                                    <label class="label">Lähetetty</label>                            
                                </div>
                                <div class="column">
                                    <span>${messageWithAttachment.message.sentAt?.format(
                    fullDateTimeFormat
                ) ?: "Ei vielä lähetetty"}</span>                            
                                </div>
                            </div>
                            <div class="columns"">
                                <div class="column is-two-fifths">
                                    <label class="label">Lähettäjä</label>                            
                                </div>
                                <div class="column">
                                    <span>${messageWithAttachment.message.senderAddress}</span>                            
                                </div>
                            </div>
                            <div class="columns">
                                <div class="column is-two-fifths">
                                    <label class="label">Vastaanottaja</label>                            
                                </div>
                                <div class="column">
                                    <span>${messageWithAttachment.message.recipientAddress}</span>                            
                                </div>
                            </div> 
                        </div>
                        <div class="columns is-multiline">
                            <div class="column is-full">
                                <label class="label">Viestin aihe</label>
                                <p>${messageWithAttachment.message.subject}</p>
                            </div>
                            <div class="column is-full">
                                <label class="label">Viesti</label>
                                <p ${addTestId("message-content")} class="message-content">${messageWithAttachment.message.body}</p>
                            </div>
                        </div>
                        ${attachmentView.renderAttachmentList(messageWithAttachment.attachments)}
                </form>
                """.trimIndent()
            ).build()
    }
}
