package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SentMessageModalView : BaseView() {
    @Autowired
    private lateinit var modal: Modal

    fun render(message: QueuedMessage): String {
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
                            <span @click="$stateId = false;" class="icon ml-m">${icons.xMark}</span>
                        </div>
                        <div class="meta-content">            
                            <div class="columns">
                                <div class="column is-two-fifths">
                                    <label class="label">Lähetetty</label>                            
                                </div>
                                <div class="column">
                                    <span>${message.sentAt?.format(fullDateTimeFormat)}</span>                            
                                </div>
                            </div>
                            <div class="columns"">
                                <div class="column is-two-fifths">
                                    <label class="label">Lähettäjä</label>                            
                                </div>
                                <div class="column">
                                    <span>${message.senderAddress}</span>                            
                                </div>
                            </div>
                            <div class="columns">
                                <div class="column is-two-fifths">
                                    <label class="label">Vastaanottaja</label>                            
                                </div>
                                <div class="column">
                                    <span>${message.recipientAddress}</span>                            
                                </div>
                            </div> 
                        </div>
                        <div class="columns is-multiline">
                            <div class="column is-full">
                                <label class="label">Viestin aihe</label>
                                <p>${message.subject}</p>
                            </div>
                            <div class="column is-full">
                                <label class="label">Viesti</label>
                                <p class="message-content">${message.body}</p>
                            </div>
                        </div>
                </form>
                """.trimIndent()
            ).build()
    }
}
