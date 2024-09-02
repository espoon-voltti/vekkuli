package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.controllers.BoatFilter
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.Harbor
import fi.espoo.vekkuli.domain.Location
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoatSpaceSearch {
    @Autowired
    lateinit var messageUtil: MessageUtil

    @Autowired
    lateinit var formComponents: FormComponents

    fun t(key: String): String {
        return messageUtil.getMessage(key)
    }

    fun render(locations: List<Location>): String {
        val boatTypes = listOf("Rowboat", "OutboardMotor", "InboardMotor", "Sailboat", "JetSki")
        val boatTypeSelect =
            formComponents.select(
                "boatApplication.boatType",
                "boatType",
                boatTypes.first(),
                boatTypes.map { it to formComponents.t("boatApplication.boatTypeOption.$it") },
            )

        val widthInput =
            formComponents.decimalInput(
                "boatApplication.boatWidthInMeters",
                "width",
                null,
            )

        val lengthInput =
            formComponents.decimalInput(
                "boatApplication.boatLengthInMeters",
                "length",
                null
            )

        val amenities = BoatSpaceAmenity.entries.toList()
        // language=HTML
        val amenitiesCheckboxes =
            """
            <label class="label">${t("boatSpaces.amenityHeader")}</label>
                <div class="field columns is-multiline is-mobile">
                ${amenities.joinToString("\n") { option ->
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
        val locations =
            """
            <label class="label">${t("boatSpaces.harborHeader")}</label>
            <div class="field columns is-multiline is-mobile">
                ${locations.joinToString("\n") { location ->
                """
                <div class="column is-half pb-none">
                    <label class="checkbox">
                        <input name="harbor" id="${location.name.decapitalize()}-checkbox" value="${location.id}" type="checkbox"/>
                        <span>${location.name}</span>
                    </label>
                </div>
                """.trimIndent()
            }}
            </div>
            """.trimIndent()

        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <div>
                        <h2>Espoon kaupungin venepaikkojen vuokraus</h2>
                        <div class="reservation-info column is-two-thirds">
                            <!-- Comment: fragments/icons :: info -->
                            <div class="info-content">
                                <p>Venepaikkoja voivat varata vain espoolaiset 01.02.2024-31.3.2024.</p>
                                <p>Muut kuin espoolaiset voivat varata venepaikkoja 01.04.2024 klo 12:00 alkaen.</p>
                                <p>Venepaikan varaaminen vaatii vahvan tunnistautumisen</p>
                            </div>
                        </div>
                    </div>
                    <div class="columns">
                        <div class="column is-two-fifths">
                            <form id="form"
                                  method="get"
                                  action="/kuntalainen/venepaikat"
                                  class="block"
                                  hx-get="/kuntalainen/partial/vapaat-paikat"
                                  hx-target="#boatSpaces"
                                  hx-swap="innerHTML"
                                  hx-trigger="input delay:1s, change">

                                <h2 class="subtitle" id="search-page-header">${t("boatApplication.boatPlaceSearchTitle")}</h2>

                                <div class="block">
                                    <div class="field">
                                        <label class="label">Haettava paikka</label>
                                        <div class="control">
                                            <label class="radio">
                                                <input type="radio" id="boatSpaceType-slip" name="boatSpaceType" value="Slip" checked/>
                                                ${t("boatSpaces.typeSlipOption")}
                                            </label>
                                            <label class="radio">
                                                <input type="radio" id="boatSpaceType-trailer" name="boatSpaceType" value="Trailer"/>
                                                ${t("boatSpaces.typeTrailerOption")}
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <div class="block">
                                    $boatTypeSelect
                                </div>

                                <div class="block">
                                    $widthInput
                                    $lengthInput

                                </div>

                                <div class="block">
                                    $amenitiesCheckboxes
                                </div>

                                <div class="block">
                                    $locations
                                </div>

                            </form>
                        </div>
                        <div class="column">
                            <div id="boatSpaces" class="block"
                                 hx-trigger="load"
                                 hx-get="/kuntalainen/partial/vapaat-paikat"
                                 hx-swap="innerHTML">
                            </div>
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
        isAuthenticated: Boolean
    ): String {
        val rowsBuilder = StringBuilder()

        harbors.forEach { harbor ->
            rowsBuilder.append(
                """
                <div class="block" x-data="{ showAll: false }">
                    <h2 class="label harbor-header">${harbor.location.name}</h2>
                    <table class="table is-striped is-hoverable is-fullwidth">
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
                    <tr ${if (index > 3) "class=\"is-hidden\"" else ""}>
                        <td>${result.formattedSizes}</td>
                        <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                        <td>${result.priceInEuro} &euro;</td>
                        <td>${result.section}${result.placeNumber}</td>
                        <td>
                    """.trimIndent()
                )

                if (isAuthenticated) {
                    rowsBuilder.append(
                        """
                        <form action="/kuntalainen/venepaikka/varaa/${result.id}" method="get">
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
                        <a class="button is-secondary reserve-button" @click="
                            openModal = true; 
                            boatSpace = {
                                id: ${result.id},
                                place: '${harbor.location.name} ${result.section}${result.placeNumber}',
                                size: '${result.formattedSizes}',
                                amenity: '${t("boatSpaces.amenityOption.${result.amenity}")}',
                                price: '${result.priceInEuro}'
                            };">
                            ${t("boatSpaces.reserve")}
                        </a>
                        """.trimIndent()
                    )
                }

                rowsBuilder.append("</td></tr>")
            }

            rowsBuilder.append("</tbody></table>")

            if (harbor.boatSpaces.size > 3) {
                rowsBuilder.append(
                    """
                    <div>
                        <a @click="showAll = !showAll" 
                           x-text="showAll ? '${t("showLess")}' : '${t("showMore")}'"></a>
                    </div>
                    """.trimIndent()
                )
            }

            rowsBuilder.append("</div>")
        }

        // Main HTML template with placeholders
        val template =
            """
            <div x-data="{
                openModal: false,
                boatSpace: {
                    id: 0
                } }">
                <h3><span>${t("boatApplication.freeSpaceCount")}</span> <span>$spaceCount</span></h3>
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
                        <p class="has-text-left" x-text="boatSpace.amenity"></p>
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
