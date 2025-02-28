package fi.espoo.vekkuli.views.employee.components

import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.addTestId
import fi.espoo.vekkuli.views.BaseView
import org.springframework.stereotype.Component

@Component
class ListFilters : BaseView() {
    fun harborFilters(
        harbors: List<Location>,
        chosenHarbors: List<Int>
    ): String {
        fun hasHarbor(id: Int): Boolean = chosenHarbors.contains(id)

        return harbors.joinToString("\n") { harbor ->
            (
                """
                <label class="filter-button" ${
                    addTestId(
                        "filter-harbor-$harbor.name"
                    )
                }>
                    <input type="checkbox" name="harbor" value="${harbor.id}" class="is-hidden" ${if (hasHarbor(
                        harbor.id
                    )
                ) {
                    "checked"
                } else {
                    ""
                }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${harbor.name}</span>
                </label>
                """.trimIndent()
            )
        }
    }

    fun boatSpaceTypeFilters(
        boatSpaceTypes: List<BoatSpaceType>,
        chosenBoatSpaceTypes: List<BoatSpaceType>
    ): String {
        fun hasBoatSpaceType(id: BoatSpaceType): Boolean = chosenBoatSpaceTypes.contains(id)
        return boatSpaceTypes.joinToString("\n") { boatSpaceType ->
            """
                <label class="filter-button" ${
                addTestId(
                    "filter-type-$boatSpaceType"
                )
            }>
                    <input type="checkbox" name="boatSpaceType" value="$boatSpaceType" class="is-hidden" ${
                if (hasBoatSpaceType(
                        boatSpaceType
                    )
                ) {
                    "checked"
                } else {
                    ""
                }
            }>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("employee.boatSpaceReservations.types.$boatSpaceType")}</span>
                </label>
            """.trimIndent()
        }
    }

    fun amenityFilters(
        amenities: List<BoatSpaceAmenity>,
        chosenAmenities: List<BoatSpaceAmenity>
    ): String {
        fun hasAmenity(amenity: BoatSpaceAmenity): Boolean = chosenAmenities.contains(amenity)
        return amenities.joinToString("\n") { amenity ->
            """
                <label class="filter-button" ${
                addTestId(
                    "filter-type-$amenity"
                )
            }>
                    <input type="checkbox" name="amenity" value="$amenity" class="is-hidden" ${
                if (hasAmenity(amenity)
                ) {
                    "checked"
                } else {
                    ""
                }
            }>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaces.amenityOption.$amenity")}</span>
                </label>
            """.trimIndent()
        }
    }

    fun reservationExpirationFilter(chosenExpiration: ReservationExpiration) =
        ReservationExpiration.entries.joinToString("\n") { state ->
            """
                <label class="filter-button" ${addTestId(
                "filter-reservation-expiration-$state"
            )}>
                    <input type="radio" name="expiration" value="${state.name}" class="is-hidden" ${if (chosenExpiration == state) {
                "checked"
            } else {
                ""
            }}>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaces.expirationOption.$state")}</span>
                </label>
            """.trimIndent()
        }

    fun paymentFilters(chosenPaymentOptions: List<PaymentFilter>): String {
        fun paymentOptions() = listOf(PaymentFilter.CONFIRMED, PaymentFilter.INVOICED, PaymentFilter.PAYMENT, PaymentFilter.CANCELLED)

        fun hasPayment(paymentOption: PaymentFilter): Boolean = chosenPaymentOptions.contains(paymentOption)
        return paymentOptions().joinToString("\n") { paymentOption ->
            """
                <label class="filter-button" ${
                addTestId(
                    "filter-reservation-state-$paymentOption"
                )
            }>
                    <input type="checkbox" name="payment" value="$paymentOption" class="is-hidden" ${
                if (hasPayment(paymentOption)) {
                    "checked"
                } else {
                    ""
                }
            }>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("boatSpaceReservation.paymentOption.${paymentOption.toString().lowercase()}")}</span>
                </label>
            """.trimIndent()
        }
    }

    fun reservationValidityFilters(chosenValidities: List<ReservationValidity>): String {
        fun hasValidity(id: ReservationValidity): Boolean = chosenValidities.contains(id)

        return ReservationValidity.entries.joinToString("\n") { validity ->
            """
                <label class="filter-button" ${
                addTestId(
                    "filter-reservation-validity-$validity"
                )
            }>
                    <input type="checkbox" name="validity" value="$validity" class="is-hidden" ${
                if (hasValidity(
                        validity
                    )
                ) {
                    "checked"
                } else {
                    ""
                }
            }>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("employee.boatReservations.validity.$validity")}</span>
                </label>
            """.trimIndent()
        }
    }

    fun boatSpaceStateFilter(chosenActiveState: List<BoatSpaceState>): String {
        fun hasState(state: BoatSpaceState): Boolean = chosenActiveState.contains(state)
        return BoatSpaceState.entries.joinToString("\n") { boatSpaceState ->
            """
                <label class="filter-button" ${
                addTestId(
                    "filter-reservation-boatSpaceState-$boatSpaceState"
                )
            }>
                    <input type="checkbox" name="boatSpaceState" value="$boatSpaceState" class="is-hidden" ${
                if (hasState(boatSpaceState)) {
                    "checked"
                } else {
                    ""
                }
            }>
                    <span class="icon is-small">
                        ${icons.check}
                    </span>
                    <span>${t("employee.boatReservations.boatSpaceState.$boatSpaceState")}</span>
                </label>
            """.trimIndent()
        }
    }
}
