package fi.espoo.vekkuli.boatSpace.citizenHome

import fi.espoo.vekkuli.service.MarkDownService
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Service

@Service
class HomeView(
    private val markDownService: MarkDownService
) : BaseView() {
    fun render(parameters: HomeViewParameters): String {
        // language=HTML
        return """
             <section class="section">
                 <div class="container">
                     <h2>${t("citizenFrontPage.title")}</h2>
                     <div class="columns">
                         <div class='column is-three-fifths'>
                             ${renderInfoColumn(icons.mapMarker, "citizenFrontPage.info.locations")}
                             ${renderInfoColumn(icons.authorization, "citizenFrontPage.info.authenticationRequired")}
                             ${renderInfoColumn(icons.boat, "citizenFrontPage.info.boatRequired")}
                             ${renderInfoColumn(icons.contactSupport, "citizenFrontPage.info.contactInfo")}
                             ${renderInfoColumn(icons.infoCircle, "citizenFrontPage.info.readMore")}
                         </div>
                         <div class="column is-align-content-center">
                             <img src="/virkailija/static/images/map-of-locations.png"
                                  alt="${t("citizenFrontPage.image.harbors.altText")}" />
                         </div>
                     </div>
                     <div class="block">
                         <a class="button is-primary" href="/kuntalainen/venepaikat">
                             ${t("citizenFrontpage.button.browseBoatSpaces")}
                         </a>
                     </div>
                 </div>
                 <div class="container">
                     <div class="container is-highlight">
                         ${renderPeriodsContainer(parameters.typeSections)}
                         <p>${t("citizenFrontpage.periods.footNote")}</p>
                     </div>
                 </div>
            </section>
            """.trimIndent()
    }

    // language=HTML
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

    private fun renderPeriodsContainer(sections: List<HomeViewSection>): String =
        sections
            .map {
                renderPeriod(it.title, it.season, it.periods)
            }.joinToString("") { it }

    private fun renderPeriod(
        periodTitle: String,
        season: String,
        periodList: List<String>
    ): String {
        val periods = periodList.map { "<p>" + markDownService.render(it) + "</p>" }.joinToString("") { it }
        // language=HTML
        return """
            <h2 class="has-text-weight-semibold">$periodTitle</h2>
            <h3 class="label">$season</h3>
            <div>
            $periods
            </div>
            """.trimIndent()
    }
}
