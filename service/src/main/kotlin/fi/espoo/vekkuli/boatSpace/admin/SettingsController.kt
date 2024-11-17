package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.utils.TimeProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class SettingsController(
    private val adminLayout: Layout,
    private val timeProvider: TimeProvider,
    private val settingsView: SettingsView
) {
    @GetMapping("/settings")
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
