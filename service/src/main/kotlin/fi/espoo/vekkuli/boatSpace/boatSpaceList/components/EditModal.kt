package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class EditModal(
    private val formComponents: FormComponents,
) : BaseView() {
    fun render(
        harbors: List<Location>,
        paymentClasses: List<Price>
    ): String {
        // language=HTML
        val harborDropdown =
            formComponents.select(
                "boatSpaceList.label.harbor",
                "harborEdit",
                "",
                harbors.map { Pair(it.id.toString(), it.name) },
                true
            )

        // language=HTML
        val sectionInput = formComponents.textInput("boatSpaceList.label.section", "sectionEdit", "", true)
        val placeNumberInput = formComponents.numberInput("boatSpaceList.label.placeNumber", "placeNumberEdit", null, true)

        val boatSpaceTypeInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceType",
                "boatSpaceTypeEdit",
                "",
                BoatSpaceType.entries.map { Pair(it.name, t("employee.boatSpaceReservations.types.${it.name}")) },
                true
            )
        val boatSpaceAmenityInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceAmenity",
                "boatSpaceAmenityEdit",
                "",
                BoatSpaceAmenity.entries.map { Pair(it.name, t("boatSpaces.amenityOption.${it.name}")) },
                true
            )
        val widthInput = formComponents.decimalInput("boatSpaceList.title.widthInMeters", "widthEdit", null, true)
        val lengthInput = formComponents.decimalInput("boatSpaceList.title.lengthInMeters", "lengthEdit", null, true)

        val paymentInput =
            formComponents.select(
                "boatSpaceList.label.paymentClass",
                "paymentEdit",
                paymentClasses[0].id.toString(),
                paymentClasses.map { Pair(it.id.toString(), it.name) },
                true
            )
        val isActiveInput =
            formComponents.radioButtons(
                "boatSpaceList.label.state",
                "boatSpaceStateEdit",
                defaultValue = BoatSpaceState.Active.name,
                options = BoatSpaceState.entries.map { RadioOption(it.name, t("boatSpaceList.boatSpaceState.${it.name}")) }
            )
        // language=HTML
        return (
            """
            <div class="modal" x-show="openEditModal" style="display:none;">
                <div class="modal-underlay" @click="openEditModal = false"></div>
                <div class="modal-content">
                    <div class="container">
                        <form id="edit-boat-space-form" class="is-1"
                            hx-post="/virkailija/venepaikat/selaa/muokkaa"
                            hx-swap="none" 
                            hx-on="htmx:afterRequest: window.location.href='/virkailija/venepaikat/selaa'">
                        <input type="hidden" name="edit" x-model="editBoatSpaces" />
                            <h2>Paikan tietojen muokkaus</h2>
                            <p class='mb-m' x-text="'Muokataan ' + editBoatSpaces.length + ' paikkaa'" > </p>
                            <div class='form-section'>             
                                <div class="columns ">
                                    <div class="column is-half">
                                        $harborDropdown
                                    </div>
                                    <template x-if='editBoatSpaces.length === 1'>
                                        <div class='columns'>
                                            <div class="column">
                                                $sectionInput
                                            </div>
                                            <div class="column">
                                                $placeNumberInput
                                            </div>
                                        </div>
                                    </template>
                                </div>
                            
                                <div class="columns">
                                    <div class="column is-half">
                                        $boatSpaceTypeInput
                                    </div>
                                    <div class="column is-half">
                                        $boatSpaceAmenityInput
                                    </div>
                                </div>
                            </div>               
                            <div class='form-section'>
                            
                                <div class="columns">
                                    <div class="column is-half">
                                        $widthInput
                                    </div>
                                    <div class="column is-half">
                                        $lengthInput
                                    </div>
                                </div>
                            </div>
                            <div class='form-section no-bottom-border'>
                                 <div class="columns">
                                    <div class="column is-half">
                                        $paymentInput
                                    </div>
                                    <div class="column is-half">
                                        $isActiveInput
                                    </div>
                                </div>
                            </div>
                            <div class="buttons is-centered">
                                <a class="button is-secondary" id="openModal-modal-cancel" x-on:click="openEditModal = false">
                                    ${t("cancel")}
                                </a>
                                <button class="button is-primary" 
                                type='submit'
                                   id="edit-modal-confirm" 
                                  >
                                    ${t("boatSpaceList.button.edit")}
                                </button>
                            </div>
                        </div>
                    </form>
                     <script>
                        validation.init({forms: ['edit-boat-space-form']})
                    </script>
                </div>
            </div>
            """.trimIndent()
        )
    }
}
