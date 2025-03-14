package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import org.springframework.stereotype.Component

@Component
class SuccessModalView(
    private val modal: Modal,
) : BaseView() {
    fun deletionModal(): String {
        val modalBuilder = modal.createModalBuilder()
        val stateId = modalBuilder.getModalStateId()
        val closeModalInMs = 3000
        return modalBuilder
            .setReloadPageOnClose(true)
            // language=HTML
            .setContent(
                modalContent(stateId, closeModalInMs, t("boatSpaceList.modal.text.deletionSuccess"), "deletion-success-modal")
            ).build()
    }

    fun creationModal(): String {
        val modalBuilder = modal.createModalBuilder()
        val stateId = modalBuilder.getModalStateId()
        val closeModalInMs = 3000
        return modalBuilder
            .setReloadPageOnClose(true)
            // language=HTML
            .setContent(
                modalContent(stateId, closeModalInMs, t("boatSpaceList.modal.text.creationSuccess"), "creation-success-modal")
            ).build()
    }

    private fun modalContent(
        stateId: String,
        closeModalInMs: Int,
        title: String,
        testId: String
    ) = """
        <div 
            class="columns pv-l is-multiline is-3" x-init="setTimeout(() => $stateId = false, $closeModalInMs)"
            ${addTestId(testId)}
        >
            <div class="column is-full has-text-centered">
                ${icons.success}
            </div>
            <div class="column is-full is-center">
                <h2 class="has-text-centered mb-none">$title</h2>
            </div>
        </div> 
        """.trimIndent()
}
