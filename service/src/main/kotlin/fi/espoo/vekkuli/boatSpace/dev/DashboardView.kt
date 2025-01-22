package fi.espoo.vekkuli.boatSpace.dev

import fi.espoo.vekkuli.boatSpace.dev.reservation.ReservationView
import fi.espoo.vekkuli.boatSpace.dev.systemTime.SetCurrentSystemTimeView
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service("DevDashboardView")
class DashboardView(
    private val setCurrentSystemTimeView: SetCurrentSystemTimeView,
    private val reservationView: ReservationView
) {
    fun render(time: LocalDateTime): String {
        // language=HTML
        return """
            ${setCurrentSystemTimeView.render(time)}
            ${reservationView.render()}
            """.trimIndent()
    }
}
