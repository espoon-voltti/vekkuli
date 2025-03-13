package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class ConfirmModal : BaseView() {
    fun render(): String =
        // language=HTML
        """
        <div class="modal" x-show="openDeleteModal" style="display:none;">
            <div class="modal-underlay" @click="openDeleteModal = false"></div>
            <div class="modal-content">
                <div class="container">
                    <div class="has-text-centered is-1">
                        <p class='mb-m' x-text="'Oletko varma että haluat poistaa ' + editBoatSpaceIds.length + ' paikkaa'" > </p>
                         <input id="boatSpaceIds" type="hidden" name="boatSpaceIds" x-model="editBoatSpaceIds" />
                        <div class="buttons is-centered">
                            <a class="button is-secondary" ${addTestId(
            "delete-modal-cancel"
        )} x-on:click="openDeleteModal = false">
                                ${t("cancel")}
                            </a>
                            <a class="button is-danger" 
                               ${addTestId("delete-modal-confirm")}
                               hx-post="/virkailija/venepaikat/selaa/poista"
                               hx-include="#boatSpaceIds">
                                ${t("confirm")}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        """.trimIndent()
}
