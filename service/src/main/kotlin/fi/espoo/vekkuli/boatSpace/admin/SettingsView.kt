package fi.espoo.vekkuli.boatSpace.admin

import fi.espoo.vekkuli.boatSpace.admin.reporting.ReportingView
import fi.espoo.vekkuli.boatSpace.admin.reservation.ReservationView
import fi.espoo.vekkuli.boatSpace.admin.systemTime.SetCurrentSystemTimeView
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SettingsView(
    private val setCurrentSystemTimeView: SetCurrentSystemTimeView,
    private val reportingView: ReportingView,
    private val reservationView: ReservationView
) {
    fun render(time: LocalDateTime): String {
        // language=HTML
        return """
            ${setCurrentSystemTimeView.render(time)}
            ${reportingView.render()}
            ${reservationView.render()}
            """.trimIndent()
    }
}
