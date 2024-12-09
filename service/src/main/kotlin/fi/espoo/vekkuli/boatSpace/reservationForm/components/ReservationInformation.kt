package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationForApplicationForm
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.utils.cmToM
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.views.BaseView
import fi.espoo.vekkuli.views.common.CommonComponents
import fi.espoo.vekkuli.views.common.ReservationInformationParams
import org.springframework.stereotype.Component

// language=HTML
@Component
class ReservationInformation(
    private val commonComponents: CommonComponents,
    private val formComponents: FormComponents
) : BaseView() {
    fun buildReservationInformationFields(reservation: ReservationForApplicationForm): ReservationInformationParams {
        val harborField =
            formComponents.field(
                "boatApplication.harbor",
                "harbor",
                reservation.locationName,
            )
        val placeField =
            formComponents.field(
                "boatApplication.place",
                "place",
                reservation.place,
            )
        val boatSpaceTypeField =
            formComponents.field(
                "boatApplication.boatSpaceType",
                "boatSpaceType",
                t("boatSpaces.typeOption.${reservation.boatSpaceType}"),
            )
        val spaceDimensionField =
            formComponents.field(
                "boatApplication.boatSpaceDimensions",
                "boatSpaceDimension",
                if (reservation.amenity != BoatSpaceAmenity.Buoy) {
                    "${reservation.widthCm.cmToM()} m x ${reservation.lengthCm.cmToM()} m"
                } else {
                    ""
                },
            )
        val amenityField =
            formComponents.field(
                "boatApplication.boatSpaceAmenity",
                "boatSpaceAmenity",
                t("boatSpaces.amenityOption.${reservation.amenity}"),
            )

        val reservationTimeField =
            formComponents.field(
                "boatSpaceReservation.label.reservationValidity",
                "reservationTime",
                if (reservation.validity === ReservationValidity.FixedTerm) {
                    """<p>${formatAsFullDate(reservation.startDate)} - ${formatAsFullDate(reservation.endDate)}</p>"""
                } else {
                    (
                        """
                    <p>${t("boatApplication.Indefinite")}</p>
                """
                    )
                },
            )
        val priceField =
            formComponents.field(
                "boatApplication.price",
                "price",
                """ <p>${t("boatApplication.boatSpaceFee")}: ${reservation.priceWithoutVatInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeAlv")}: ${reservation.vatPriceInEuro} &euro;</p>
                <p>${t("boatApplication.boatSpaceFeeTotal")}: ${reservation.priceInEuro} &euro;</p>""",
            )
        return ReservationInformationParams(
            harborField,
            placeField,
            boatSpaceTypeField,
            spaceDimensionField,
            amenityField,
            reservationTimeField,
            priceField
        )
    }

    fun render(
        reservation: ReservationForApplicationForm,
        params: ReservationInformationParams = buildReservationInformationFields(reservation)
    ): String {
        // language=HTML
        val reservationInformationFields =
            commonComponents.reservationInformationFields(params)

        // language=HTML
        return """
            <h3 class="header">${t("boatApplication.boatSpaceInformation")}</h3>
            $reservationInformationFields
            
            """.trimIndent()
    }
}
