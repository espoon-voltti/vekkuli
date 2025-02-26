package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.RadioOption
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.BoatFilter
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Service

@Service
class BoatSpaceSearch(
    private val messageUtil: MessageUtil,
    private val formComponents: FormComponents,
    private val markDownService: MarkDownService,
    private val icons: Icons,
    private val stepIndicator: StepIndicator
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(
        locations: List<Location>,
        winterLocations: List<Int>,
        isEmployee: Boolean = false
    ): String {
        val boatTypes = BoatType.entries.map { it.name }
        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "boatType",
                null,
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
            )

        val boatWidthInput =
            formComponents.decimalInput(
                "boatSpaces.boatWidthHeader",
                "width",
                null,
                required = true,
            )

        val boatLengthInput =
            formComponents.decimalInput(
                "boatSpaces.boatLengthHeader",
                "length",
                null,
                required = true,
            )

        val spaceWidthInput =
            formComponents.decimalInput(
                "boatSpaces.spaceWidthHeader",
                "width",
                null,
                required = true,
            )

        val spaceLengthInput =
            formComponents.decimalInput(
                "boatSpaces.spaceLengthHeader",
                "length",
                null,
                required = true,
            )

        val trailerLengthInput =
            formComponents.decimalInput(
                "boatSpaces.trailerLengthHeader",
                "length",
                null,
                required = true,
            )

        val trailerWidthInput =
            formComponents.decimalInput(
                "boatSpaces.trailerWidthHeader",
                "width",
                null,
                required = true,
            )

        val storageTypeButtons =
            formComponents.radioButtons(
                "boatSpaces.storageTypeHeader",
                "amenities",
                StorageType.Trailer.name,
                storageTypeAmenities.map { RadioOption(it.name, t("boatSpaces.amenityOption.$it")) },
                mapOf("x-model" to "amenities"),
                isColumnLayout = true
            )

        val storageType =
            // language=HTML
            """
             <div class="block" x-show="boatSpaceType === 'Storage'" x-data="{amenities: ''}">
                $storageTypeButtons
            </div>            
            """.trimIndent()

        // language=HTML
        val amenitiesCheckboxes =
            """
            <label class="label">${t("boatSpaces.amenityHeader")}</label>
                <div class="field columns is-multiline is-mobile">
                ${baseAmenities.joinToString("\n") { option ->
                """
                <div class="column is-half pb-none">
                    <label class="checkbox">
                        <input name="amenities" id="${option.toString().decapitalize()}-checkbox" value="$option" type="checkbox"/>
                        <span>${t("boatSpaces.amenityOption.$option")}</span>
                    </label>
                </div>
                """.trimIndent()
            }}
                </div>
            """.trimIndent()

        // language=HTML
        val locationsCheckboxes =
            """
            <label class="label">${t("boatSpaces.harborHeader")}</label>
            <div class="field columns is-multiline is-mobile">
                ${locations.joinToString("\n") { location ->
                val visibility = if (!(location.id in winterLocations)) "x-show=\"boatSpaceType != 'Winter'\"" else ""
                """
                <div class="column is-half pb-none" $visibility>
                    <label class="checkbox">
                        <input name="harbor" id="${location.name.decapitalize()}-checkbox" value="${location.id}" type="checkbox"/>
                        <span>${location.name}</span>
                    </label>
                </div>
                """.trimIndent()
            }}
            </div>
            """.trimIndent()

        val infoBox =
            """
            <div class="mb-xl">
                <div class="container is-highlight">
                    <h2 class="has-text-weight-semibold">${t("boatSpaces.infoText.title")}</h2>
                    <h3 class="label">${t("boatSpaces.infoText.periods.newReservations")}</h3>
                    <h3 class="label">${t("boatSpaces.infoText.periods.trailerReservations")}</h3>
                    <h3 class="label">${t("boatSpaces.infoText.periods.winter")}</h3>
                    <h3 class="label">${t("boatSpaces.infoText.periods.storage1")}</h3>
                    <h3 class="label">${t("boatSpaces.infoText.periods.storage2")}</h3>
                </div>
            </div>
            """.trimIndent()

        val spaceTypeSelection =
            formComponents.radioButtons(
                "boatSpaces.typeHeader",
                "boatSpaceType",
                "Slip",
                listOf("Slip", "Trailer", "Winter", "Storage").map {
                    RadioOption(it, t("boatSpaces.typeOption.$it"), t("boatSpaces.typeOptionLabel.$it"))
                },
                mapOf("x-model" to "boatSpaceType")
            )

        val typeSelect =
            """
             <div class="block">
               $spaceTypeSelection
            </div>            
            """.trimIndent()

        val url = "/${if (isEmployee)"virkailija" else "kuntalainen"}/partial/vapaat-paikat"
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    ${stepIndicator.render(1)}
                
                    <div>
                        <h2>${t("boatApplication.title.search")}</h2>
                    </div>
                    ${if (!isEmployee) infoBox else ""}
                    <div class="columns">
                        <div class="column is-two-fifths" x-data="{boatSpaceType: 'Slip', storageType: 'Trailer'}">
                            <form id="form"
                                  method="get"
                                  action="$url"
                                  class="block"
                                  hx-get="$url"
                                  hx-target="#boatSpaces"
                                  hx-swap="innerHTML"
                                  hx-trigger="load, input delay:500ms"
                                  hx-sync="closest #form:replace"
                                  hx-indicator="#loader, .loaded-content"
                                  >

                                <h2 class="subtitle" id="search-page-header">${t("boatApplication.boatPlaceSearchTitle")}</h2>

                               $typeSelect
                               
                               <div class="block" x-show="boatSpaceType != 'Storage'">
                                   $locationsCheckboxes
                               </div>
                               
                               $storageType

                               
                                <template x-if="boatSpaceType === 'Slip'">
                                    <div class="block">
                                        $boatTypeSelect
                                        <div class="columns">
                                            <div class='column'>
                                                $boatWidthInput
                                            </div>
                                            <div class='column'>
                                                $boatLengthInput
                                            </div>
                                        </div>
                                    </div >
                                </template>
                                
                                <template x-if="boatSpaceType === 'Trailer'">
                                    <div class="columns">
                                        <div class='column'>
                                            $trailerWidthInput
                                        </div>
                                        <div class='column'>
                                            $trailerLengthInput
                                        </div>
                                    </div>
                                </template>
                                
                                <template x-if="boatSpaceType != 'Slip' && boatSpaceType != 'Trailer'">
                                    <div class='block'>
                                        <div class="columns">
                                            <div class='column'>
                                                $spaceWidthInput
                                            </div>
                                            <div class='column'>
                                                $spaceLengthInput
                                            </div>
                                        </div>
                                        <div class="columns">
                                        <div class='column'>
                                            <p class="mt-s information-text">${t("boatSpaces.text.spaceSizeInfo")}</p>
                                            </div>
                                        </div>
                                    </div>
                                </template>

                                <div class="block" x-show="boatSpaceType === 'Slip'">
                                    $amenitiesCheckboxes
                                </div>


                            </form>
                            <script>
                                document.getElementById('form').addEventListener('change', function(event) {
                                    const data = new FormData(event.currentTarget);
                                    localStorage.setItem('boatSpaceType', data.get('boatSpaceType'));
                                    localStorage.setItem('boatType', data.get('boatType'));
                                    localStorage.setItem('width', data.get('width'));
                                    localStorage.setItem('length', data.get('length'));
                                });
                            </script>
                            <div class="mt-xl">
                               <img src="/virkailija/static/images/map-of-locations.png"
                                    alt="${t("citizenFrontPage.image.harbors.altText")}" />
                            </div>
                        </div>
                        <div class="column">
                            <div id="boatSpaces" class="block loaded-content">
                            </div>
                            <div id="loader" class="htmx-indicator"> ${icons.spinner} <div>
                        </div>
                    </div>
                </div>
            </section>
            """.trimIndent()
    }

    fun renderResults(
        harbors: List<Harbor>,
        boat: BoatFilter,
        spaceCount: Int,
        isAuthenticated: Boolean,
        isEmployee: Boolean = false
    ): String {
        val rowsBuilder = StringBuilder()
        // language=HTML
        harbors.forEach { harbor ->
            rowsBuilder.append(
                """
                <div class="block" x-data="{ show: 5 }">
                    <div class='mb-m'>
                        <h3 class="subtitle harbor-header mb-s">${harbor.location.name}</h3>
                        <p class="body">${harbor.location.address}</p>
                    </div>
                    
                 ${if (harbor.location.id == 955) {
                    """
                    <div class="ack-info">
                        <div class="info-icon">${icons.warningExclamation(false)}</div>
                        <div class="info-content">
                            ${t("boatSpace.infoText.otsolahtiSailingBoat")}
                        </div>
                    </div>
                    """.trimIndent()
                } else {
                    ""
                }}
                   
                    <table class="table search-results-table is-striped is-hoverable is-fullwidth">
                        <thead>
                            <tr>
                                <th>${t("boatSpaces.size")}</th>
                                <th>${t("boatSpaces.amenityLabel")}</th>
                                <th>${t("boatSpaces.price")}</th>
                                <th>${t("boatSpaces.place")}</th>
                            </tr>
                        </thead>
                        <tbody>
                """.trimIndent()
            )

            harbor.boatSpaces.forEachIndexed { index, result ->
                rowsBuilder.append(
                    """
                    <tr :class="${index + 1} <= show  ? '' : 'is-hidden' ">
                        <td>${result.formattedSizes}</td>
                        <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                        <td>${result.formattedPrice} &euro;</td>
                        <td>${result.place}</td>
                        <td>
                    """.trimIndent()
                )

                if (isAuthenticated) {
                    val url = "/${if (isEmployee)"virkailija" else "kuntalainen"}/venepaikka/varaa/${result.id}"
                    rowsBuilder.append(
                        """
                        <form action="$url" method="get">
                            <input type="hidden" name="boatType" value="${boat.type ?: ""}" />
                            <input type="hidden" name="width" value="${boat.width ?: ""}" />
                            <input type="hidden" name="length" value="${boat.length ?: ""}" />
                            <button type="submit" class="button is-primary reserve-button">
                                ${t("boatSpaces.reserve")}
                            </button>
                        </form>
                        """.trimIndent()
                    )
                } else {
                    rowsBuilder.append(
                        """
                        <button class="button is-primary reserve-button" @click="
                            openModal = true; 
                            boatSpace = {
                                id: ${result.id},
                                place: '${harbor.location.name} ${result.place}',
                                size: '${result.formattedSizes}',
                                amenity: '${if (result.amenity != BoatSpaceAmenity.None) {
                            t(
                                "boatSpaces.amenityOption.${result.amenity}"
                            )
                        } else {
                            ""
                        }}',
                                price: '${result.formattedPrice}'
                            };">
                            ${t("boatSpaces.reserve")}
                        </button>
                        """.trimIndent()
                    )
                }

                rowsBuilder.append("</td></tr>")
            }

            rowsBuilder.append("</tbody></table>")

            rowsBuilder.append(
                """
                <span style="margin-right: 16px">
                    <a x-show="show < ${harbor.boatSpaces.size}" 
                        @click="show = Math.min(show + 5, ${harbor.boatSpaces.size})">
                        <span class="icon is-small">
                             ${icons.chevronDown}
                        </span>
                        <span x-text="`${t("showMore")} (${"$"}{${harbor.boatSpaces.size} - show})`"></span>
                    </a>
                </span>
                <span>
                    <a x-show="show > 5" @click="show = Math.max(show - 5, 5)">
                        <span class="icon is-small">
                             ${icons.chevronUp}
                        </span>
                       <span>${t("showLess")}</span>
                    </a>
                </span>
                """.trimIndent()
            )

            rowsBuilder.append("</div>")
        }

        // language=HTML
        val searchResultHeader =
            """<h3><span>${t("boatApplication.freeSpaceCount")}</span> <span>($spaceCount)</span></h3> """

        // language=HTML
        val template =
            """
            <div x-data="{
                openModal: false,
                boatSpace: {
                    id: 0
                } }">
                $searchResultHeader
                 ${if (boat.length == null || boat.width == null) {
                """<div id="empty-dimensions-warning" class="reservation-info column is-four-fifths">
                <div class="column is-narrow">
                <span class="icon">
                    ${icons.info}
                </span>
                </div>
                
                <p class="column">${t("boatApplication.noFreeSpaces")}</p></div> """
            } else {
                ""
            }}
                <!-- Insert dynamically generated rows here -->
                $rowsBuilder
                
                ${if (!isAuthenticated) {
                """
                <div id="auth-modal" class="modal" x-show="openModal" style="display:none;">
                    <div class="modal-underlay" @click="openModal = false"></div>
                    <div class="modal-content">
                        <p class="block has-text-left">${t("auth.reservingBoatSpace")}</p>
                        <p class="has-text-left" x-text="boatSpace.place"></p>
                        <p class="has-text-left" x-text="boatSpace.size"></p>
                        <p x-if="!!boatSpace.amenity" class="has-text-left" x-text="boatSpace.amenity"></p>
                        <p class="has-text-left block" x-text="boatSpace.price + ' &euro;'"></p>
                        <p class="has-text-left block">${t("auth.reservingRequiresAuth")}</p>
                        <button id="auth-modal-cancel" class="button" @click="openModal = false" type="button">
                            ${t("cancel")}
                        </button>
                        <a id="auth-modal-continue" class="button is-primary" 
                           x-bind:href="'/auth/saml-suomifi/login?RelayState=/kuntalainen/venepaikka/varaa/' + boatSpace.id">
                            ${t("auth.continue")}
                        </a>
                    </div>
                </div>
                """.trimIndent()
            } else {
                ""
            }}
            </div>
            """.trimIndent()

        return template
    }
}

fun String.decapitalize(): String {
    if (isEmpty()) {
        return this
    }
    return this.substring(0, 1).lowercase() + this.substring(1)
}
