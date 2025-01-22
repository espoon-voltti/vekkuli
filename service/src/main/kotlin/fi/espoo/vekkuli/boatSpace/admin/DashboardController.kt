package fi.espoo.vekkuli.boatSpace.admin

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("AdminDashboardController")
@RequestMapping("/virkailija/admin")
class DashboardController(
    private val adminLayout: Layout,
    private val settingsView: DashboardView
) {
    @GetMapping("/dashboard")
    @ResponseBody
    fun setSystemDateView() =
        ResponseEntity
            .ok()
            .header("Content-Type", "text/html")
            .body(
                adminLayout.render(
                    settingsView
                        .render()
                )
            )
}
