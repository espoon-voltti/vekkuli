package fi.espoo.vekkuli.boatSpace.employeeReservationList.components

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.employee.components.ExpandingFilter
import org.springframework.stereotype.Component

@Component
class AmenityFilter(
    private val expandingFilter: ExpandingFilter,
) : BaseView() {
    fun render(
        selectedAmenities: List<BoatSpaceAmenity>,
        amenities: List<BoatSpaceAmenity>,
    ): String =
        expandingFilter.filterDropdown(
            selectedAmenities.map { t ->
                t.name
            },
            "amenity",
            amenities.joinToString("") { amenityCheckbox(it.name) }
        )

    private fun amenityCheckbox(amenity: String) =
        //language=HTML
        """
        <label class="checkbox dropdown-item" style="margin-bottom:4px;" ${addTestId("filter-amenity-$amenity")}>
            <input type="checkbox" name="amenity" value="$amenity" x-model="amenity" />
            <span>${t("boatSpaces.amenityOption.$amenity")}</span>
        </label>
        """.trimIndent()
}
