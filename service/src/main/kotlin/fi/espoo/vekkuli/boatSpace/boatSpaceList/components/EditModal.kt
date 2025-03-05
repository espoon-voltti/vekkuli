package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import java.math.BigDecimal
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
            """
            <div class="field">
                        <label class="label">Satama</label>
                        <div class="control">
                            <div class="select">
                                <select>
                                ${
                harbors.joinToString("\n") { harbor ->
                    "<option value='${harbor.id}'>${harbor.name}</option>"
                }}   
                                </select>
                            </div>
                        </div>
                    </div>
            """.trimIndent()

        // language=HTML
        val sectionInput = formComponents.textInput("boatSpaceList.label.section", "sectionEdit", "")
        val placeNumberInput = formComponents.textInput("boatSpaceList.label.placeNumber", "placeNumberEdit", "")

        val boatSpaceTypeInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceType",
                "boatSpaceTypeEdit",
                "",
                BoatSpaceType.entries.map { Pair(it.name, t("employee.boatSpaceReservations.types.${it.name}")) },
            )
        val boatSpaceAmenityInput =
            formComponents.select(
                "boatSpaceList.label.boatSpaceAmenity",
                "boatSpaceAmenityEdit",
                "",
                BoatSpaceAmenity.entries.map { Pair(it.name, t("boatSpaces.amenityOption.${it.name}")) }
            )
        val widthInput = formComponents.decimalInput("boatSpaceList.title.widthInMeters", "widthEdit", BigDecimal(0))
        val lengthInput = formComponents.decimalInput("boatSpaceList.title.lengthInMeters", "lengthEdit", BigDecimal(0))

        val paymentInput =
            formComponents.select(
                "boatSpaceList.label.paymentClass",
                "paymentEdit",
                "",
                paymentClasses.map { Pair(it.id.toString(), it.name) }
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
                        <div class="is-1">
                            <h2>Paikan tietojen muokkaus</h2>
                            <p class='mb-m' x-text="'Muokataan ' + editBoatSpaces.length + ' paikkaa'" > </p>
                            <div class="columns">
                                <div class="column is-half">
                                    $harborDropdown
                                </div>
                                <div class="column is-one-quarter">
                                    $sectionInput
                                </div>
                                <div class="column is-one-quarter">
                                    $placeNumberInput
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
                            <div class="columns">
                                <div class="column is-half">
                                    $widthInput
                                </div>
                                <div class="column is-half">
                                    $lengthInput
                                </div>
                            </div>
                             <div class="columns">
                                <div class="column is-half">
                                    $paymentInput
                                </div>
                                <div class="column is-half">
                                    $isActiveInput
                                </div>
                            </div>
                            <div class="buttons is-centered">
                                <a class="button is-secondary" id="openModal-modal-cancel" x-on:click="openEditModal = false">
                                    ${t("cancel")}
                                </a>
                                <a class="button is-danger" 
                                   id="edit-modal-confirm" 
                                   hx-post="/virkailija/venepaikat/selaa/muokkaa"
                                   hx-swap="none" 
                                   hx-on="htmx:afterRequest: window.location.href='/virkailija/venepaikat/selaa'">
                                    ${t("boatSpaceList.button.edit")}
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """.trimIndent()
        )
    }
}
