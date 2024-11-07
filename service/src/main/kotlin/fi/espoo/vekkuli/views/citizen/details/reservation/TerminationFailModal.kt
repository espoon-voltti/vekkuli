package fi.espoo.vekkuli.views.citizen.details.reservation

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.Icons
import fi.espoo.vekkuli.views.components.modal.Modal
import fi.espoo.vekkuli.views.components.modal.ModalButtonStyle
import fi.espoo.vekkuli.views.components.modal.ModalButtonType
import org.springframework.stereotype.Component

@Component
class TerminationFailModal(
    private val modal: Modal,
    private val icons: Icons
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
                        <h2 class="has-text-centered mb-none">${t("boatSpaceTermination.messages.terminationFailed")}</h2>
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
