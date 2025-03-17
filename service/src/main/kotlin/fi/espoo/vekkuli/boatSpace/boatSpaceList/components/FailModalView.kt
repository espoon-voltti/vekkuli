package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import fi.espoo.vekkuli.views.components.modal.ModalButtonStyle
import fi.espoo.vekkuli.views.components.modal.ModalButtonType
import org.springframework.stereotype.Component

enum class DeletionError {
    DELETION_FAILED,
    BOAT_SPACE_HAS_RESERVATIONS,
}

@Component
class FailModalView(
    private val modal: Modal,
) : BaseView() {
    fun deletionModal(type: DeletionError = DeletionError.DELETION_FAILED): String {
        val message =
            when (type) {
                DeletionError.DELETION_FAILED -> t("boatSpaceList.modal.text.deletionFailed")
                DeletionError.BOAT_SPACE_HAS_RESERVATIONS -> t("boatSpaceList.modal.text.deletionFailedReservations")
            }
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            // language=HTML
            .setContent(
                modalContent(message, "deletion-fail-modal")
            ).addButton {
                setText(t("button.ok"))
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Danger)
                setTestId("delete-modal-ok")
            }.setButtonsCentered(true)
            .build()
    }

    fun creationModal(): String {
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            // language=HTML
            .setContent(
                modalContent(t("boatSpaceList.modal.text.creationFailed"), "create-reservation-fail-modal")
            ).addButton {
                setText(t("button.ok"))
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Danger)
                setTestId("create-modal-ok")
            }.setButtonsCentered(true)
            .build()
    }

    private fun modalContent(
        title: String,
        testId: String
    ) = """
         <div class="columns is-multiline is-2" ${addTestId(testId)}>
            <div class="column is-full has-text-centered">
                ${icons.errorNotification}
            </div>
            <div class="column is-full">
                <h2 class="has-text-centered mb-none">$title</h2>
            </div>
        </div>
        """.trimIndent()
}
