package fi.espoo.vekkuli.views.citizen

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
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
                        <input name="amenities" id="${option.toString().lowercase()}-checkbox" value="$option" type="checkbox"/>
                        ${t("boatSpaces.amenityOption.$option")}
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
                        <input name="harbor" id="${location.name.lowercase()}-checkbox" value="${location.id}" type="checkbox"/>
                        ${location.name}
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
                                                <input type="radio" id="boatSpaceType-slip" name="boatSpaceType" value="Slip"/>
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
}
