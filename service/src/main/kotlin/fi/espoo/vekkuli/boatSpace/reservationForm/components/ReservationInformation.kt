package fi.espoo.vekkuli.boatSpace.reservationForm.components

import fi.espoo.vekkuli.FormComponents
import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationForApplicationForm
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.utils.formatAsFullDate
import fi.espoo.vekkuli.utils.formatInt
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
    fun reservationInformationWithStorageType(reservation: ReservationForApplicationForm): String {
        // language=HTML
        val storageTypeInformationField =
            """
            <div class='field' >
               <label class="label">${t("boatApplication.title.boatSpaceStorageType")}</label>
               <template x-if="storageType === '${StorageType.Trailer.name}'">
                <p id="storage-type-text-trailer">${t("boatSpaces.storageType.Trailer")}</p>
              </template>
            
              <template x-if="storageType === '${StorageType.Buck.name}'">
                <p id="storage-type-text-buck">${t("boatSpaces.storageType.Buck")}</p>
              </template>
            
              <template x-if="storageType === '${StorageType.BuckWithTent.name}'">
                <p id="storage-type-text-buckTent">${t("boatSpaces.storageType.BuckWithTent")}</p>
              </template>
               
            </div>
            """.trimIndent()

        val informationFields = buildReservationInformationFields(reservation)
        val informationFieldsWithDynamicAmenity =
            informationFields.copy(
                amenityField = storageTypeInformationField
            )
        return render(reservation, informationFieldsWithDynamicAmenity)
    }

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
                    "${formatInt(reservation.widthCm)} m x ${formatInt(reservation.lengthCm)} m"
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

        val reservationValidityField =
            formComponents.field(
                "boatSpaceReservation.label.reservationValidity",
                "reservationValidity",
                """
                <div class='field' >
                   <template x-if="reservationValidity === '${ReservationValidity.FixedTerm}'">
                     <p id="reservation-validity-fixedTerm">${formatAsFullDate(
                    reservation.startDate
                )} - ${formatAsFullDate(reservation.endDate)}</p>
                   </template>
                  
                  <template x-if="reservationValidity === '${ReservationValidity.Indefinite}'">
                     <p id="reservation-validity-indefinite">${t("boatApplication.Indefinite")}</p>
                  </template>
                </div>        
                """.trimIndent(),
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
            reservationValidityField,
            priceField
        )
    }

    fun render(
        reservation: ReservationForApplicationForm,
        params: ReservationInformationParams = buildReservationInformationFields(reservation),
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
