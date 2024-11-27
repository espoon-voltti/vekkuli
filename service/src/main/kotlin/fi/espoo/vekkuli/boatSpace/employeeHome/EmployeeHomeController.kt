package fi.espoo.vekkuli.boatSpace.employeeHome

import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.views.employee.EmployeeHome
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class EmployeeHomeController(
    private val employeeLayout: EmployeeLayout,
    private val employeeHome: EmployeeHome,
    private val jdbi: Jdbi,
) {
    @GetMapping("/virkailija")
    @ResponseBody
    fun homePage(
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
