package fi.espoo.vekkuli.boatSpace.dev

import fi.espoo.vekkuli.utils.TimeProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController("DevDashboardController")
@RequestMapping("/dev")
class DashboardController(
    private val adminLayout: Layout,
    private val timeProvider: TimeProvider,
    private val settingsView: DashboardView
) {
    @GetMapping("/dashboard")
    @ResponseBody
    fun setSystemDateView(request: HttpServletRequest) =
        ResponseEntity
            .ok()
            .header("Content-Type", "text/html")
            .body(
                adminLayout.render(
                    settingsView
                        .render(
                            timeProvider.getCurrentDateTime()
                        )
                )
            )
}
