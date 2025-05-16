package fi.espoo.vekkuli.boatSpace.boatSpaceDetails

import fi.espoo.vekkuli.boatSpace.boatSpaceList.components.BoatSpaceRow
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.formatAsFullDateTime
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class BoatSpaceHistory(
    val reserverId: UUID,
    val reserverType: ReserverType,
    val reserverName: String,
    val reserverPhoneNumber: String,
    val reservationStatus: ReservationStatus,
    val reservationCreateDate: LocalDateTime,
    val reservationEndDate: LocalDate,
)

@Service
class BoatSpaceDetails(
    private val boatSpaceRow: BoatSpaceRow,
    private val commonComponents: CommonComponents
) : BaseView() {
    fun render(
        boatSpaceName: String,
        boatSpaceReservationHistory: List<BoatSpaceHistory>
    ): String {
        fun historyRow(reservation: BoatSpaceHistory) =
            """
             <tr>
                <td><a href=${boatSpaceRow.getBoatSpacePage(
                reservation.reserverId,
                reservation.reserverType
            )}>${reservation.reserverName}</a></td>
                <td>${reservation.reserverPhoneNumber}</td>
                <td>${t("boatSpaceDetails.reservationStatus.${reservation.reservationStatus}")}</td>
                <td>${formatAsFullDateTime(reservation.reservationCreateDate)}</td>
                <td>${formatAsFullDate(reservation.reservationEndDate)}</td>
            </tr>
            """.trimIndent()

        val reservationHistoryRows =
            boatSpaceReservationHistory
                .mapIndexed { index, reservation -> historyRow(reservation) }
                .joinToString("")

        // language=HTML
        return """
            <div class='section'>
                <div class='container block'>
                ${commonComponents.goBackButton("/virkailija/venepaikat/selaa")}
                   <h2>$boatSpaceName</h2>
                </div>
                <div class='container block'>
                        <h3>${t("boatSpaceDetails.history.title")}</h3>
                        <div class='table-container'>
                            <table class='table is-hoverable'>
                                <thead>
                                    <tr>
                                        <th>${t("boatSpaceDetails.header.reserver")}</th>
                                        <th>${t("boatSpaceDetails.header.phoneNumber")}</th>
                                        <th>${t("boatSpaceDetails.header.reservationStatus")}</th>
                                        <th>${t("boatSpaceDetails.header.reservationCreated")}</th>
                                        <th>${t("boatSpaceDetails.header.reservationValidUntil")}</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    $reservationHistoryRows
                                </tbody>
                            </table>
                        </div>
                </div>
            </div>
            """.trimIndent()
    }
}
