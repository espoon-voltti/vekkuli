package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class MarkAsPaidConfirmModal : BaseView() {
    // language=html
    fun render() =
        (
            """
            <div id="confirm-modal" x-show="confirmModalOpen" class="modal" style="display: none;">
                <div class="modal-underlay" @click="confirmModalOpen = false"></div>
                <div class="modal-content">
                    <p class="block has-text-left">
                        ${t("invoice.modal.text")}
                    </p>
                    <!-- Cancel button inside modal -->
                    <button id="confirm-modal-cancel"
                            class="button"
                            @click="confirmModalOpen = false"
                            type="button">
                        ${t("cancel")}
                    </button>
                    <!-- Confirm button inside modal -->
                    <button id="confirm-modal-submit"
                            class="button is-primary"
                            type="submit"
                            @click="markAsPaidInputValue = true; confirmModalOpen = false">
                        ${t("invoice.modal.button.confirm")}
                    </button>
                </div>
            </div>

            """.trimIndent()
        )
}
