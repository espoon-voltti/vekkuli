package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListRow
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.domain.toPath
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component
import org.springframework.web.util.HtmlUtils.htmlEscape
import java.util.*

@Component
class BoatSpaceRow : BaseView() {
    fun getReserverPage(
        reserverId: UUID?,
        reserverType: ReserverType?
    ) = if (reserverId !== null) {
        "/virkailija/${reserverType?.toPath()}/$reserverId"
    } else {
        ""
    }

    // language=HTML
    fun editCheckBox(result: BoatSpaceListRow) =
        """
             <label class="checkbox">
                <input class="boat-space-checkbox" name="spaceId"  ${addTestId(
            "edit-boat-space-${result.id}"
        )} type="checkbox" value="${result.id}" x-model='editBoatSpaceIds' />
            </label>
        """.trimIndent()

    // language=HTML
    fun render(result: BoatSpaceListRow): String =
        """
                <tr class="boat-space-item"
                ${addTestId("boat-space-${result.id}")}>
                    <td>${editCheckBox(result)}</td>
                    <td>${result.locationName}</td>
                    <td ${
            addTestId(
                "place"
            )
        }><a href=${getBoatSpaceDetailsUrl(result.id)}>
                        ${result.place}</a></td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                   
                    <td>${result.widthInMeter}</td>
                    <td>${result.lengthInMeter}</td>
                    <td>${result.priceClass}</td>
                    <td>${result.priceInEuro}</td>
                    <td> <span id='status-ball' class=${if (result.active) "active" else "inactive"}></span></td>
                    <td>${reserverCell(result)}</td>
                </tr>
        """.trimIndent()

    private fun reserverCell(result: BoatSpaceListRow): String {
        val inactiveReason = result.inactiveReason?.takeIf { !result.active }
        val reserverLink =
            """<a href=${getReserverPage(result.reserverId, result.reserverType)} >${htmlEscape(result.reserverName ?: "-")}</a>"""
        if (inactiveReason == null) return reserverLink

        val reasonText = """<p class="information-text" ${addTestId("inactive-reason")}>${htmlEscape(inactiveReason)}</p>"""
        return if (result.reserverName != null) reserverLink + reasonText else reasonText
    }

    private fun getBoatSpaceDetailsUrl(id: Int): String = "/virkailija/venepaikat/$id"
}
