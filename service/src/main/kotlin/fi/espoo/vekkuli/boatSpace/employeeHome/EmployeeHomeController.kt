package fi.espoo.vekkuli.boatSpace.employeeHome

import fi.espoo.vekkuli.common.BlankLayoutView
import fi.espoo.vekkuli.common.getAppUser
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.controllers.Utils.Companion.redirectUrl
import fi.espoo.vekkuli.views.employee.EmployeeHome
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class EmployeeHomeController(
    private val blankLayoutView: BlankLayoutView,
    private val employeeHome: EmployeeHome,
    private val jdbi: Jdbi,
) {
    @GetMapping("/virkailija")
    @ResponseBody
    fun homePage(
        request: HttpServletRequest,
        model: Model
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        val isAuthenticatedEmployee = authenticatedUser?.type == "user"
        if (isAuthenticatedEmployee) {
            return redirectUrl("/virkailija/venepaikat/varaukset")
        }
        val user =
            authenticatedUser?.let {
                jdbi.inTransactionUnchecked { tx ->
                    tx.getAppUser(authenticatedUser.id)
                }
            }
        return ResponseEntity.ok(blankLayoutView.render(employeeHome.render()))
    }
}
