package fi.espoo.vekkuli.boatSpace.admin.reporting

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedEmployee
import fi.espoo.vekkuli.service.boatSpaceReportToCsv
import fi.espoo.vekkuli.service.freeBoatSpaceReportToCsv
import fi.espoo.vekkuli.service.getAllBoatSpacesReport
import fi.espoo.vekkuli.service.getFreeBoatSpaceReportRows
import fi.espoo.vekkuli.service.getReservedBoatSpaceReport
import fi.espoo.vekkuli.service.getStickerReportRows
import fi.espoo.vekkuli.service.getTerminatedBoatSpaceReport
import fi.espoo.vekkuli.service.getWarningsBoatSpaceReportRows
import fi.espoo.vekkuli.service.stickerReportToCsv
import fi.espoo.vekkuli.service.terminatedBoatSpaceReportToCsv
import fi.espoo.vekkuli.service.warningsBoatSpaceReportToCsv
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.views.employee.EmployeeLayout
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.jdbi.v3.core.Jdbi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/virkailija/admin/reporting")
class ReportingController(
    private val reportingLayout: Layout,
    private val reportingView: ReportingView
) {
    @Autowired
    private lateinit var timeProvider: TimeProvider

    @Autowired
    lateinit var jdbi: Jdbi

    @Autowired
    lateinit var layout: EmployeeLayout

    private val logger = KotlinLogging.logger {}

    val utf8BOM = "\uFEFF"

    @GetMapping("/sticker-report", produces = ["text/csv"])
    @ResponseBody
    fun stickerReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_STICKER_REPORT")

        val minCreationDate = reportingDate ?: timeProvider.getCurrentDate()
        val dateFormatted = minCreationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-tarraraportti-$dateFormatted.csv\"")
            .body(utf8BOM + stickerReportToCsv(getStickerReportRows(jdbi, minCreationDate)))
    }

    @GetMapping("/boat-space-report", produces = ["text/csv"])
    @ResponseBody
    fun boatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_BOAT_SPACE_REPORT")

        val reportDate = reportingDate?.atStartOfDay() ?: timeProvider.getCurrentDateTime()
        val todayFormatted = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-venepaikkaraportti-$todayFormatted.csv\"")
            .body(utf8BOM + boatSpaceReportToCsv(getAllBoatSpacesReport(jdbi, reportDate)))
    }

    @GetMapping("/boat-space-report/free", produces = ["text/csv"])
    @ResponseBody
    fun freeBoatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_FREE_BOAT_SPACE_REPORT")

        val reportDate = reportingDate?.atStartOfDay() ?: timeProvider.getCurrentDateTime()
        val todayFormatted = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-vapaat-paikat-raportti-$todayFormatted.csv\"")
            .body(utf8BOM + freeBoatSpaceReportToCsv(getFreeBoatSpaceReportRows(jdbi, reportDate)))
    }

    @GetMapping("/boat-space-report/reserved", produces = ["text/csv"])
    @ResponseBody
    fun reservedBoatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_RESERVED_BOAT_SPACE_REPORT")

        val reportDate = reportingDate?.atStartOfDay() ?: timeProvider.getCurrentDateTime()
        val todayFormatted = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-varatut-paikat-raportti-$todayFormatted.csv\"")
            .body(utf8BOM + boatSpaceReportToCsv(getReservedBoatSpaceReport(jdbi, reportDate)))
    }

    @GetMapping("/boat-space-report/terminated", produces = ["text/csv"])
    @ResponseBody
    fun terminatedBoatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_TERMINATED_BOAT_SPACE_REPORT")

        val reportDate = reportingDate?.atStartOfDay() ?: timeProvider.getCurrentDateTime()
        val todayFormatted = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-irtisanotut-paikat-raportti-$todayFormatted.csv\"")
            .body(utf8BOM + terminatedBoatSpaceReportToCsv(getTerminatedBoatSpaceReport(jdbi, reportDate)))
    }

    @GetMapping("/boat-space-report/warnings", produces = ["text/csv"])
    @ResponseBody
    fun warningsBoatSpaceReport(
        request: HttpServletRequest,
        @RequestParam("reportingDate") reportingDate: LocalDate?,
    ): ResponseEntity<String> {
        logger.audit(request.getAuthenticatedEmployee(), "DOWNLOAD_WARNINGS_BOAT_SPACE_REPORT")

        val reportDate = reportingDate?.atStartOfDay() ?: timeProvider.getCurrentDateTime()
        val todayFormatted = reportDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return ResponseEntity
            .ok()
            .header("Content-Disposition", "attachment; filename=\"vekkuli-varoitukset-raportti-$todayFormatted.csv\"")
            .body(utf8BOM + warningsBoatSpaceReportToCsv(getWarningsBoatSpaceReportRows(jdbi, reportDate)))
    }

    @GetMapping
    @ResponseBody
    fun getReportingView(request: HttpServletRequest) =
        ResponseEntity
            .ok(
                layout.render(
                    true,
                    request.requestURI,
                    reportingLayout.render(reportingView.render())
                )
            )
}
