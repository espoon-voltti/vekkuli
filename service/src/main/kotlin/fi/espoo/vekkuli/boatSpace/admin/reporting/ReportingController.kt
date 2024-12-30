package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedEmployee
import fi.espoo.vekkuli.service.getRawReport
import fi.espoo.vekkuli.service.getStickerReport
import fi.espoo.vekkuli.service.rawReportToCsv
import fi.espoo.vekkuli.service.stickerReportToCsv
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
    lateinit var jdbi: Jdbi

    private val logger = KotlinLogging.logger {}

    val utf8BOM = "\uFEFF"

    @GetMapping("/raw-report", produces = ["text/csv"])
    @ResponseBody
    fun rawReport(request: HttpServletRequest): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_RAW_REPORT")

        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-raakaraportti-$today.csv\"")
            .body(rawReportToCsv(getRawReport(jdbi)))
    }

    @GetMapping("/sticker-report", produces = ["text/csv"])
    @ResponseBody
    fun stickerReport(request: HttpServletRequest): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_STICKER_REPORT")

        val today = LocalDate.now()
        val todayFormatted = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-tarraraportti-$todayFormatted.csv\"")
            .body(utf8BOM + stickerReportToCsv(getStickerReport(jdbi, today)))
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
