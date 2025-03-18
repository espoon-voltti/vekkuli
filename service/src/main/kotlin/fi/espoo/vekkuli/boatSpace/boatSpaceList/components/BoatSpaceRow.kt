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
    fun getBoatSpacePage(
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
                    <td 
                        ${
            addTestId(
                "place"
            )
        }>${result.place}</td>
                    <td>${t("employee.boatSpaceReservations.types.${result.type}")}</td>
                    <td>${t("boatSpaces.amenityOption.${result.amenity}")}</td>
                   
                    <td>${result.widthInMeter}</td>
                    <td>${result.lengthInMeter}</td>
                    <td>${result.priceClass}</td>
                    <td>${result.priceInEuro}</td>
                    <td> <span id='status-ball' class=${if (result.isActive) "active" else "inactive"}></span></td>
                    <td> <a href=${getBoatSpacePage(
            result.reserverId,
            result.reserverType
        )} >${htmlEscape((result.reserverName ?: '-').toString())}</a></td>
                </tr>
        """.trimIndent()
}
