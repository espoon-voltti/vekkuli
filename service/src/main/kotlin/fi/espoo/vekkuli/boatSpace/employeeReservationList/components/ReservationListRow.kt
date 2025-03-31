package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.BoatSpaceReservationItem
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.utils.formatAsShortYearDate
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import org.springframework.web.util.HtmlUtils.htmlEscape
import java.util.UUID

@Component
class ReservationListRow : BaseView() {
    fun render(reservation: BoatSpaceReservationItem): String {
        val statusText = getStatusText(reservation)
        val startDateText = formatAsShortYearDate(reservation.startDate)
        val endDateText = getEndDateText(reservation)
        val reserverUrl = getReserverPageUrl(reservation.reserverId, reservation.reserverType)
        val warningIcon = getWarningIcon(reservation.hasAnyWarnings())

        //language=HTML
        return """
            <tr class="reservation-item"
                id="boat-space-${reservation.boatSpaceId}"
                hx-trigger="click"
                hx-get=$reserverUrl
                hx-push-url="true"
                hx-target=".section"
                hx-select=".section">
                <td>$warningIcon</td>
                <td>${reservation.locationName}</td>
                <td>
                    <span ${addTestId("place")}>${reservation.place}</span>
                </td>
                <td>${t("employee.boatSpaceReservations.types.${reservation.type}")}</td>
                <td>${t("boatSpaces.amenityOption.${reservation.getBoatSpaceAmenity()}")}</td>
                <td ${addTestId("reserver-name")}>
                    <span class='link'>${htmlEscape(reservation.name)}</span>
                </td>
                <td>${htmlEscape(reservation.phone)}</td>
                <td ${addTestId("reserver-email")}>${htmlEscape(reservation.email)}</td>
                <td>${reservation.municipalityName}</td>
                <td>$statusText</td>
                <td ${addTestId("reservation-start-date")}>$startDateText</td>
                <td ${addTestId("reservation-end-date")}>$endDateText</td>
            </tr>
            """.trimIndent()
        //language=HTML
    }

    private fun getStatusText(reservation: BoatSpaceReservationItem): String =
        when (reservation.status) {
            ReservationStatus.Confirmed -> getConfirmedStatusText(reservation)
            ReservationStatus.Invoiced -> getInvoicedStatusText(reservation)
            else -> t("boatSpaceReservation.paymentOption.${reservation.status.toString().lowercase()}")
        }

    private fun getConfirmedStatusText(reservation: BoatSpaceReservationItem): String {
        val paymentPart =
            reservation.paymentDate?.let {
                ", " + t("employee.boatSpaceReservations.paidDate") +
                    " " + formatAsShortYearDate(it)
            } ?: ""

        return t("boatSpaceReservation.paymentOption.confirmed") + paymentPart
    }

    private fun getInvoicedStatusText(reservation: BoatSpaceReservationItem): String {
        val dueDatePart =
            reservation.invoiceDueDate?.let {
                ", " + t("employee.boatSpaceReservations.dueDate") +
                    " " + formatAsShortYearDate(it)
            } ?: ""

        return t("boatSpaceReservation.paymentOption.invoiced") + dueDatePart
    }

    private fun getEndDateText(reservation: BoatSpaceReservationItem): String {
        val endDateFormatted = formatAsShortYearDate(reservation.endDate)
        return if (reservation.status == ReservationStatus.Cancelled) {
            """<span class="has-text-danger">${t("reservations.text.terminated")} $endDateFormatted</span>"""
        } else if (reservation.validity == ReservationValidity.FixedTerm) {
            endDateFormatted
        } else {
            ""
        }
    }

    private fun getReserverPageUrl(
        reserverId: UUID,
        reserverType: ReserverType
    ) = """"/virkailija/${if (reserverType == ReserverType.Citizen) "kayttaja" else "yhteiso"}/$reserverId""""

    private fun getWarningIcon(hasWarnings: Boolean) =
        if (hasWarnings) {
            "<div ${addTestId("warning-icon")}>${icons.warningExclamation(false)}</div>"
        } else {
            ""
        }
}
