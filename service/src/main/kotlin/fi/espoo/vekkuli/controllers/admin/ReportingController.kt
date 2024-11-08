package fi.espoo.vekkuli.controllers.admin

import fi.espoo.vekkuli.common.Forbidden
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.service.getRawReport
import fi.espoo.vekkuli.service.rawReportToCsv
import fi.espoo.vekkuli.views.admin.Layout
import fi.espoo.vekkuli.views.admin.ReportingView
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/reporting")
class ReportingController(
    private val reportingLayout: Layout,
    private val reportingView: ReportingView
) {
    @Autowired
    lateinit var jdbi: Jdbi

    private val logger = KotlinLogging.logger {}

    @GetMapping("/raw-report", produces = ["text/csv"])
    @ResponseBody
    fun rawReport(request: HttpServletRequest): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Forbidden("No authenticated user")
        logger.audit(authenticatedUser, "DOWNLOAD_RAW_REPORT")
        return ResponseEntity.ok(rawReportToCsv(getRawReport(jdbi)))
    }

    @GetMapping
    @ResponseBody
    fun getReportingView(request: HttpServletRequest) =
        ResponseEntity
            .ok()
            .header("Content-Type", "text/html")
            .body(
                reportingLayout.render(
                    reportingView.render()
                )
            )
}
