package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.util.*

@Component
class CreateBoatSpaceModal(
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
                "harborCreation",
                "",
                harborOptions,
                placeholder = "",
                required = true
            )

        val sectionInput = formComponents.textInput("boatSpaceList.label.section", "sectionCreation", null)
        val placeNumberInput = formComponents.numberInput("boatSpaceList.label.placeNumber", "placeNumberCreation", null, true)

        val boatSpaceTypeInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceType",
                "boatSpaceTypeCreation",
                "",
                BoatSpaceType.entries.map { Pair(it.name, t("employee.boatSpaceReservations.types.${it.name}")) },
                placeholder = "",
                required = true
            )
        val boatSpaceAmenityInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceAmenity",
                "boatSpaceAmenityCreation",
                "",
                BoatSpaceAmenity.entries.map { Pair(it.name, t("boatSpaces.amenityOption.${it.name}")) },
                required = true
            )
        val widthInput = formComponents.decimalInput("boatSpaceList.title.widthInMeters", "widthCreation", null, true)
        val lengthInput = formComponents.decimalInput("boatSpaceList.title.lengthInMeters", "lengthCreation", null, true)

        val paymentInput =
            formComponents.select(
                "boatSpaceList.label.paymentClass",
                "paymentCreation",
                "",
                paymentClasses.map { Pair(it.id.toString(), it.name) },
                required = true
            )
        val isActiveInput =
            formComponents.radioButtons(
                "boatSpaceList.label.state",
                "boatSpaceStateCreation",
                defaultValue = BoatSpaceState.Active.name,
                options =
                    BoatSpaceState.entries.map { RadioOption(it.name, t("boatSpaceList.boatSpaceState.${it.name}")) }
            )
        // language=HTML
        return (
            """
            <div class="modal" x-show="openCreateModal" style="display:none;">
                <div class="modal-underlay" @click="openCreateModal = false"></div>
                <div class="modal-content">
                    <div class="container">
                        <form id="create-boat-space-form" class="is-1"
                            hx-post="/virkailija/venepaikat/lisaa"
                            hx-swap ="innerHTML"
                            hx-target="#modal-container"
                            hx-on:htmx:after-settle="document.getElementById('create-boat-space-button').disabled = false; document.getElementById('create-boat-space-button').classList.remove('is-loading')"
>
                            <h2>Uuden venepaikan luonti</h2>
                            <div class='form-section'>             
                                <div class="columns ">
                                    <div class="column is-half">
                                        $harborDropdown
                                    </div>
                                    <div class='columns'>
                                        <div class="column">
                                            $sectionInput
                                        </div>
                                        <div class="column">
                                            $placeNumberInput
                                        </div>
                                    </div>
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
                                <a class="button is-secondary" x-on:click="openCreateModal = false">
                                    ${t("cancel")}
                                </a>
                                <button id="create-boat-space-button" class="button is-primary" type='submit' ${addTestId(
                "create-modal-confirm"
            )}>
                                    ${t("boatSpaceList.button.create")}
                                </button>
                            </div>
                            <script>
                                validation.init({forms: ['create-boat-space-form']})
                            </script>
                        </form> 
                    </div>
                </div>
            </div>
            """.trimIndent()
        )
    }
}
