package fi.espoo.vekkuli.boatSpace.citizenHome

import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.Layout
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class CitizenHomeController(
    private val layout: Layout,
    private val homeView: HomeView,
    private val citizenService: CitizenService,
    private val citizenHomeService: CitizenHomeService
) {
    @GetMapping("/")
    @ResponseBody
    fun homePage(
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = getCitizen(request, citizenService)
        val isAuthenticatedCitizen = user != null

        return layout.render(
            isAuthenticatedCitizen,
            user?.fullName,
            request.requestURI,
            homeView.render(citizenHomeService.getHomeViewParameters())
        )
    }
}
