package fi.espoo.vekkuli.boatSpace.reservationEndDate

import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.components.modal.Modal
import org.springframework.stereotype.Component

@Component
class ReservationEndDateSuccessModalView(
    private val modal: Modal
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
                    ${addTestId("reservation-end-date-success-modal")}
                >
                    <div class="column is-full has-text-centered">
                        ${icons.success}
                    </div>
                    <div class="column is-full is-center">
                        <h2 class="has-text-centered mb-none">${t("reservationEndDate.messages.success")}</h2>
                    </div>
                </div>
                """.trimIndent()
            ).build()
    }
}
