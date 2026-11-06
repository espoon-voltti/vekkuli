package fi.espoo.vekkuli.boatSpace.boatSpaceList.components

import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.views.BaseView

fun BaseView.amenityOptionText(amenity: BoatSpaceAmenity): String =
    if (amenity == BoatSpaceAmenity.None) {
        t("boatSpaceList.amenityOption.None")
    } else {
        t("boatSpaces.amenityOption.${amenity.name}")
    }
