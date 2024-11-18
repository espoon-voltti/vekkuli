package fi.espoo.vekkuli.boatSpace.terminateReservation.modal

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.modal.Modal
import org.springframework.stereotype.Component

@Component
class TerminationSuccessModalView(
    private val modal: Modal,
    private val icons: Icons
) : BaseView() {
    fun render(): String {
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
                    ${addTestId("termination-success-modal")}
                >
                    <div class="column is-full has-text-centered">
                        ${icons.success}
                    </div>
                    <div class="column is-full is-center">
                        <h2 class="has-text-centered mb-none">${t("boatSpaceTermination.messages.success")}</h2>
                    </div>
                </div> 
                """.trimIndent()
            ).build()
    }
}
