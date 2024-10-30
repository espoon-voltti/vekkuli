// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.utils.centsToEuro
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt

enum class ReservationStatus {
    Info,
    Renewal,
    Payment,
    Confirmed,
    Cancelled,
    Invoiced
}

data class BoatSpace(
    val id: Int,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val excludedBoatTypes: List<BoatType>? = null,
    val locationName: String?,
)

data class BoatSpaceReservation(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: ReservationStatus,
    val actingUserId: UUID?,
    val reserverId: UUID?,
    val paymentId: UUID?,
    val validity: ReservationValidity
)

fun getPriceWithoutAlv(priceCents: Int) = (priceCents / (1.0 + (BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE / 100.0))).roundToInt()

data class ReservationWithDependencies(
    val id: Int,
    val boatId: Int?,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: ReservationStatus,
    val actingUserId: UUID?,
    val reserverId: UUID?,
    val employeeId: UUID?,
    val reserverType: ReserverType?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val locationName: String,
    val priceCents: Int,
    val excludedBoatTypes: List<BoatType>?,
    val validity: ReservationValidity? = ReservationValidity.Indefinite,
    val renewedFromId: Int? = null,
) {
    val priceInEuro: Double
        get() = priceCents.centsToEuro()
    val alvPriceInEuro: Double
        get() = (priceCents - getPriceWithoutAlv(priceCents)).centsToEuro()
    val priceWithoutAlvInEuro: Double
        get() = getPriceWithoutAlv(priceCents).centsToEuro()
}

data class BoatSpaceReservationItem(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val reserverId: UUID,
    val actingUserId: UUID?,
    val reserverType: ReserverType,
    val name: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val place: String,
    val section: String,
    val locationName: String,
    val boatRegistrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val municipalityCode: Int,
    val municipalityName: String,
    val warnings: Set<String> = emptySet()
) {
    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}

data class BoatSpaceReservationItemWithWarning(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val reserverId: UUID,
    val actingUserId: UUID?,
    val reserverType: ReserverType,
    val name: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val boatRegistrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val warning: String?,
    val section: String,
    val municipalityCode: Int,
    val municipalityName: String,
)

enum class BoatSpaceFilterColumn {
    START_DATE,
    END_DATE,
    PLACE,
    PLACE_TYPE,
    HOME_TOWN,
    CUSTOMER,
    BOAT,
}

enum class PaymentFilter {
    PAID,
    UNPAID,
}

data class BoatSpaceReservationFilter(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.PLACE,
    val ascending: Boolean = false,
    val amenity: List<BoatSpaceAmenity> = emptyList(),
    val harbor: List<Int> = emptyList(),
    val payment: List<PaymentFilter> = emptyList(),
    val nameSearch: String? = null,
    val warningFilter: Boolean? = null,
    val sectionFilter: List<String> = emptyList(),
) {
    fun hasHarbor(id: Int): Boolean = harbor.contains(id)

    fun hasAmenity(id: BoatSpaceAmenity): Boolean = amenity.contains(id)

    fun hasPayment(paymentFilter: PaymentFilter): Boolean = payment.contains(paymentFilter)
}

fun getSortingSql(sort: BoatSpaceReservationFilter): String {
    val sortDir = if (!sort.ascending) " DESC" else ""
    return when (sort.sortBy) {
        BoatSpaceFilterColumn.START_DATE -> "ORDER BY start_date$sortDir"
        BoatSpaceFilterColumn.END_DATE -> "ORDER BY end_date$sortDir"
        BoatSpaceFilterColumn.PLACE -> "ORDER BY location_name$sortDir, place$sortDir"
        BoatSpaceFilterColumn.PLACE_TYPE -> "ORDER BY type$sortDir"
        BoatSpaceFilterColumn.CUSTOMER -> "ORDER BY full_name$sortDir"
        BoatSpaceFilterColumn.HOME_TOWN -> "ORDER BY home_town$sortDir"
        BoatSpaceFilterColumn.BOAT -> "ORDER BY boat_registration_code$sortDir"
    }
}
