package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.views.employee.EmployeeLayout
import fi.espoo.vekkuli.views.organization.OrganizationDetails
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
class OrganizationUserController(
    private val organizationDetails: OrganizationDetails,
    private val employeeLayout: EmployeeLayout,
) {
    @GetMapping("/virkailija/yhteiso/{organizationId}")
    @ResponseBody
    fun citizenProfile(
        request: HttpServletRequest,
        @PathVariable organizationId: UUID,
    ): String =
        employeeLayout.render(
            true,
            request.requestURI,
            organizationDetails.organizationPage(organizationId)
        )
}
