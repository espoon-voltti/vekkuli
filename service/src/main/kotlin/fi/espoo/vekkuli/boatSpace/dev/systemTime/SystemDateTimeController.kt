package fi.espoo.vekkuli.boatSpace.dev.systemTime

import fi.espoo.vekkuli.boatSpace.admin.Layout
import fi.espoo.vekkuli.service.VariableService
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/dev")
class SystemDateTimeController(
    private val variable: VariableService,
    private val adminLayout: Layout,
    private val timeProvider: TimeProvider,
    private val setCurrentSystemTime: SetCurrentSystemTimeView
) {
    @PostMapping("/set-system-date")
    fun setSystemDate(
        @RequestParam newSystemDate: LocalDate?,
    ): ResponseEntity<Void> {
        val variableId = "current_system_staging_datetime"
        if (newSystemDate == null) {
            variable.delete(variableId)
        } else {
            val dateTime: LocalDateTime = newSystemDate.atTime(LocalTime.of(0, 0, 0))
            variable.set(variableId, dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        }
        return ResponseEntity.ok().build()
    }

    @GetMapping("/get-system-date")
    fun getSystemDate(): ResponseEntity<String> =
        ResponseEntity.ok(
            timeProvider.getCurrentDateTime().format(DateTimeFormatter.ISO_DATE_TIME)
        )

    @GetMapping("/set-system-date")
    @ResponseBody
    fun setSystemDateView() =
        ResponseEntity
            .ok()
            .header("Content-Type", "text/html")
            .body(
                adminLayout.render(
                    setCurrentSystemTime.render(
                        timeProvider.getCurrentDateTime()
                    )
                )
            )
}
