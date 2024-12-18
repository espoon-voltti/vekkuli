// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.centsToEuro
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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
    val validity: ReservationValidity
)

data class ReservationWithDependencies(
    val id: Int,
    val boatId: Int?,
    val trailerId: Int?,
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
    val storageType: StorageType?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val place: String,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val locationName: String,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val excludedBoatTypes: List<BoatType>?,
    val validity: ReservationValidity? = ReservationValidity.Indefinite,
    val renewedFromId: Int? = null,
) {
    val priceInEuro: String
        get() = priceCents.centsToEuro()
    val vatPriceInEuro: String
        get() = vatCents.centsToEuro()
    val priceWithoutVatInEuro: String
        get() = netPriceCents.centsToEuro()
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
    val boat: Boat?,
    val trailer: Trailer?,
    val storageType: StorageType?,
    val municipalityCode: Int,
    val municipalityName: String,
    val paymentDate: LocalDate?,
    val warnings: Set<String> = emptySet(),
    val validity: ReservationValidity?
) {
    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()
}

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

enum class ReservationExpiration {
    Active,
    Expired,
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
    val expiration: ReservationExpiration = ReservationExpiration.Active,
    val boatSpaceType: List<BoatSpaceType> = emptyList(),
) {
    fun hasHarbor(id: Int): Boolean = harbor.contains(id)

    fun hasBoatSpaceType(id: BoatSpaceType): Boolean = boatSpaceType.contains(id)

    fun hasAmenity(id: BoatSpaceAmenity): Boolean = amenity.contains(id)

    fun hasPayment(paymentFilter: PaymentFilter): Boolean = payment.contains(paymentFilter)
}
