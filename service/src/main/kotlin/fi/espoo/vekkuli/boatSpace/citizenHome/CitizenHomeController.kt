package fi.espoo.vekkuli.boatSpace.citizenHome

import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.views.citizen.Layout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class CitizenHomeController(
    private val layout: Layout,
    private val homeView: HomeView,
    private val reserverService: ReserverService,
    private val citizenHomeService: CitizenHomeService
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/")
    @ResponseBody
    fun homePage(
        request: HttpServletRequest,
        model: Model
    ): String {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "CITIZEN_HOME")
        }
        val user = getCitizen(request, reserverService)
        val isAuthenticatedCitizen = user != null

        return layout.render(
            isAuthenticatedCitizen,
            user?.fullName,
            request.requestURI,
            homeView.render(citizenHomeService.getHomeViewParameters())
        )
    }
}
