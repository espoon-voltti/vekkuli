package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.Home
import fi.espoo.vekkuli.views.citizen.Layout
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
    @Autowired
    lateinit var layout: Layout

    @Autowired
    lateinit var home: Home

    @Autowired
    lateinit var citizenService: CitizenService

    @GetMapping("/")
    @ResponseBody
    fun citizenHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = getCitizen(request, citizenService)
        val isAuthenticatedCitizen = user != null

        return layout.generateLayout(isAuthenticatedCitizen, user?.fullName, home.render())
    }

    @GetMapping("/virkailija")
    fun employeeHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        return "home"
    }
}
