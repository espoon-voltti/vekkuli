package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedEmployee
import fi.espoo.vekkuli.service.boatSpaceReportToCsv
import fi.espoo.vekkuli.service.getBoatSpaceReport
import fi.espoo.vekkuli.service.getStickerReport
import fi.espoo.vekkuli.service.stickerReportToCsv
import fi.espoo.vekkuli.utils.TimeProvider
import jakarta.servlet.http.HttpServletRequest
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/admin/reporting")
class ReportingController(
    private val reportingLayout: Layout,
    private val reportingView: ReportingView
) {
    @Autowired
    private lateinit var timeProvider: TimeProvider

    @Autowired
    lateinit var jdbi: Jdbi

    private val logger = KotlinLogging.logger {}

    val utf8BOM = "\uFEFF"

    @GetMapping("/sticker-report", produces = ["text/csv"])
    @ResponseBody
    fun stickerReport(
        request: HttpServletRequest,
        @RequestParam("startDate") startDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_STICKER_REPORT")

        val now = startDate?.atStartOfDay()
        val todayFormatted = now?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "kaikki"
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-tarraraportti-$todayFormatted.csv\"")
            .body(utf8BOM + stickerReportToCsv(getStickerReport(jdbi, now)))
    }

    @GetMapping("/boat-space-report", produces = ["text/csv"])
    @ResponseBody
    fun boatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("startDate") startDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_BOAT_SPACE_REPORT")

        val now = startDate?.atStartOfDay()
        val todayFormatted = now?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "kaikki"
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-venepaikkaraportti-$todayFormatted.csv\"")
            .body(utf8BOM + boatSpaceReportToCsv(getBoatSpaceReport(jdbi, now)))
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
