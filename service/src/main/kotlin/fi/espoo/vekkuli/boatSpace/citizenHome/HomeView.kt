package fi.espoo.vekkuli.boatSpace.citizenHome

import fi.espoo.vekkuli.config.MessageUtil
import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.Icons
import org.springframework.stereotype.Service

@Service
class HomeView(
    private val messageUtil: MessageUtil,
    private val markDownService: MarkDownService,
    private val icons: Icons
) {
    fun t(key: String): String = messageUtil.getMessage(key)

    fun render(): String {
        // language=HTML
        return """
            <section class="section">
                <div class="container">
                    <h2>${t("boatSpaces.title")}</h2>
                    <div class="columns">
                        <div class='column is-three-fifths'>
                            ${renderInfoColumn(icons.mapMarker, "citizenFrontPage.info.locations")}
                            ${renderInfoColumn(icons.authorization, "citizenFrontPage.info.authenticationRequired")}
                            ${renderInfoColumn(icons.boat, "citizenFrontPage.info.boatRequired")}
                            ${renderInfoColumn(icons.contactSupport, "citizenFrontPage.info.contactInfo")}
                            ${renderInfoColumn(icons.infoCircle, "citizenFrontPage.info.readMore")}
                        </div>
                        <div class="column is-align-content-center">
                            <img src="/static/images/map-of-locations.png" alt="${t("citizenFrontPage.image.harbors.altText")}" />
                        </div>
                    </div>
                    <div class="block">
                        <a class="button is-primary" href="/kuntalainen/venepaikat">
                            ${t("home.reserveBoatSpace")}
                        </a>
                    </div>
            </section>
            """.trimIndent()
    }

    private fun renderInfoColumn(
        icon: String,
        key: String
    ) = """
        <div class="columns">
            <div class="column is-narrow">
                 <span class="icon is-medium">
                    $icon
                  </span>
            </div>
            <div class="column">
                <p>${markDownService.render(t(key))}</p>
            </div>
        </div>
        """.trimIndent()
}
