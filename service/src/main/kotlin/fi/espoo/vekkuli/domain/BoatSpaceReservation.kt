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
    Payment,
    Confirmed,
    Cancelled
}

data class BoatSpaceReservation(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: ReservationStatus,
    val citizenId: UUID,
)

fun getAlvPriceInCents(priceCents: Int) = (priceCents / (1.0 + (BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE / 100.0))).roundToInt()

data class ReservationWithDependencies(
    val id: Int,
    val boatId: Int?,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: ReservationStatus,
    val citizenId: UUID,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val locationName: String,
    val priceCents: Int,
) {
    val priceInEuro: Double
        get() = priceCents.centsToEuro()
    val alvPriceInEuro: Double
        get() = getAlvPriceInCents(priceCents).centsToEuro()
    val priceWithoutAlvInEuro: Double
        get() = (priceCents - getAlvPriceInCents(priceCents)).centsToEuro()
}

data class BoatSpaceReservationItem(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val citizenId: UUID,
    val firstName: String,
    val lastName: String,
    val homeTown: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val boatRegistrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val warnings: List<String> = emptyList()
) {
    val showOwnershipWarning: Boolean
        get() = boatOwnership == OwnershipStatus.FutureOwner || boatOwnership == OwnershipStatus.CoOwner
}

data class BoatSpaceReservationItemWithWarning(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val citizenId: UUID,
    val firstName: String,
    val lastName: String,
    val homeTown: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val boatRegistrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val warning: String?
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

data class BoatSpaceReservationFilter(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.PLACE,
    val ascending: Boolean = false,
    val amenity: List<BoatSpaceAmenity> = emptyList(),
    val harbor: List<Int> = emptyList(),
) {
    fun toggleSort(name: String): String {
        val value = BoatSpaceFilterColumn.valueOf(name)
        if (sortBy == value) {
            return this.copy(ascending = !ascending).getQueryParams()
        } else {
            return this.copy(sortBy = value).getQueryParams()
        }
    }

    fun toggleHarbor(id: Int): String {
        if (harbor.contains(id)) {
            return this.copy(harbor = harbor - id).getQueryParams()
        } else {
            return this.copy(harbor = harbor + id).getQueryParams()
        }
    }

    fun toggleAmenity(name: String): String {
        val value = BoatSpaceAmenity.valueOf(name)
        if (amenity.contains(value)) {
            return this.copy(amenity = amenity - value).getQueryParams()
        } else {
            return this.copy(amenity = amenity + value).getQueryParams()
        }
    }

    fun hasHarbor(id: Int): Boolean = harbor.contains(id)

    fun hasAmenity(id: BoatSpaceAmenity): Boolean = amenity.contains(id)

    fun getQueryParams(): String {
        val params = mutableListOf<String>()
        params.add("sortBy=$sortBy")
        params.add("ascending=$ascending")
        amenity.forEach {
            params.add("amenity=$it")
        }
        harbor.forEach {
            params.add("harbor=$it")
        }
        return "?${params.joinToString("&")}"
    }

    fun getSortForColumn(name: String): String {
        val value = BoatSpaceFilterColumn.valueOf(name)
        return if (sortBy == value) {
            if (ascending) "asc" else "desc"
        } else {
            ""
        }
    }
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
