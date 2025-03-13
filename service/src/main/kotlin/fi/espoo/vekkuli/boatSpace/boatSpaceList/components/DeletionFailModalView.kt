package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import fi.espoo.vekkuli.views.components.modal.ModalButtonStyle
import fi.espoo.vekkuli.views.components.modal.ModalButtonType
import org.springframework.stereotype.Component

@Component
class DeletionFailModalView(
    private val modal: Modal,
) : BaseView() {
    fun render(): String {
        val modalBuilder = modal.createModalBuilder()
        return modalBuilder
            // language=HTML
            .setContent(
                """
                 <div class="columns is-multiline is-2" ${addTestId("termination-fail-modal")}>
                    <div class="column is-full has-text-centered">
                        ${icons.errorNotification}
                    </div>
                    <div class="column is-full">
                        <h2 class="has-text-centered mb-none">${t("boatSpaceList.modal.text.failed")}</h2>
                    </div>
                </div>
                """.trimIndent()
            ).addButton {
                setText(t("button.ok"))
                setType(ModalButtonType.Cancel)
                setStyle(ModalButtonStyle.Danger)
                setTestId("terminate-reservation-fail-modal-ok")
            }.setButtonsCentered(true)
            .build()
    }
}
