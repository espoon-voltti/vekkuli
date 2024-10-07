package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.getCitizen
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.views.citizen.EmployeeHome
import fi.espoo.vekkuli.views.citizen.Home
import fi.espoo.vekkuli.views.citizen.Layout
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
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
    lateinit var employeeLayout: EmployeeLayout

    @Autowired
    lateinit var employeeHome: EmployeeHome

    @Autowired
    lateinit var citizenService: CitizenService

    @Autowired
    lateinit var jdbi: Jdbi

    @GetMapping("/")
    @ResponseBody
    fun citizenHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        val user = getCitizen(request, citizenService)
        val isAuthenticatedCitizen = user != null

        return layout.render(isAuthenticatedCitizen, user?.fullName, request.requestURI, home.render())
    }

    @GetMapping("/virkailija")
    @ResponseBody
    fun employeeHome(
        request: HttpServletRequest,
        model: Model
    ): String {
        val authenticatedUser = request.getAuthenticatedUser()
        val isAuthenticatedEmployee = authenticatedUser?.type == "user"
        val user =
            authenticatedUser?.let {
                jdbi.inTransactionUnchecked { tx ->
                    tx.getAppUser(authenticatedUser.id)
                }
            }
        val userName = "${user?.firstName} ${user?.lastName}"
        return employeeLayout.render(isAuthenticatedEmployee, userName, employeeHome.render(isAuthenticatedEmployee, userName))
    }
}
