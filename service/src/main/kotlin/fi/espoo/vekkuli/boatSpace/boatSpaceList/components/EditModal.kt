package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
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
        val harborOptions = harbors.map { Pair(it.id.toString(), it.name) }

        val harborDropdown =
            formComponents.select(
                "boatSpaceList.label.harbor",
                "harbor",
                "",
                listOf(Pair("", "Ei valittu")) + harborOptions,
                placeholder = ""
            )

        val sectionInput = formComponents.textInput("boatSpaceList.label.section", "section", null)
        val placeNumberInput = formComponents.numberInput("boatSpaceList.label.placeNumber", "placeNumber", null)

        val boatSpaceTypeInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceType",
                "boatSpaceType",
                "",
                listOf(Pair("", "Ei valittu")) +
                    BoatSpaceType.entries.map { Pair(it.name, t("employee.boatSpaceReservations.types.${it.name}")) },
                placeholder = ""
            )
        val boatSpaceAmenityInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceAmenity",
                "boatSpaceAmenity",
                "",
                listOf(Pair("", "Ei valittu")) + BoatSpaceAmenity.entries.map { Pair(it.name, t("boatSpaces.amenityOption.${it.name}")) },
            )
        val widthInput = formComponents.decimalInput("boatSpaceList.title.widthInMeters", "width", null)
        val lengthInput = formComponents.decimalInput("boatSpaceList.title.lengthInMeters", "length", null)

        val paymentInput =
            formComponents.select(
                "boatSpaceList.label.paymentClass",
                "payment",
                "",
                listOf(Pair("", "Ei valittu")) + paymentClasses.map { Pair(it.id.toString(), it.name) },
            )
        val isActiveInput =
            formComponents.radioButtons(
                "boatSpaceList.label.state",
                "boatSpaceState",
                defaultValue = "",
                options =
                    listOf(RadioOption("", "Ei valittu")) +
                        BoatSpaceState.entries.map { RadioOption(it.name, t("boatSpaceList.boatSpaceState.${it.name}")) }
            )
        // language=HTML
        return (
            """
            <div class="modal" x-show="openEditModal" style="display:none;">
                <div class="modal-underlay" @click="openEditModal = false"></div>
                <div class="modal-content">
                    <div class="container">
                        <form class="is-1"
                            hx-post="/virkailija/venepaikat/selaa/muokkaa"
                            hx-swap="none" 
                            hx-on="htmx:afterRequest: window.location.href='/virkailija/venepaikat/selaa'">
                        <input type="hidden" name="boatSpaceIds" x-model="editBoatSpaceIds" />
                            <h2>Paikan tietojen muokkaus</h2>
                            <p ${addTestId("target-boat-space-count")} class='mb-m'
                                x-text="'Muokataan ' + editBoatSpaceIds.length + ' paikkaa'" > </p>
                            <div class='form-section'>             
                                <div class="columns ">
                                    <div class="column is-half">
                                        $harborDropdown
                                    </div>
                                    <template x-if='editBoatSpaceIds.length === 1'>
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
                                <a class="button is-secondary" id="edit-modal-cancel" x-on:click="openEditModal = false">
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
                </div>
            </div>
            """.trimIndent()
        )
    }
}
