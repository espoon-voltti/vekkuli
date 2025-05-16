// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.discountedPriceInCents
import fi.espoo.vekkuli.utils.formatInt
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

enum class ReservationStatus {
    Info,
    Payment,
    Confirmed,
    Cancelled,
    Invoiced
}

enum class CreationType {
    Switch,
    Renewal,
    New
}

data class BoatSpace(
    val id: Int,
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val place: String,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val excludedBoatTypes: List<BoatType>? = null,
    val locationName: String?,
    val locationAddress: String?,
    val locationId: Int?,
    val isActive: Boolean,
)

data class BoatSpaceReservation(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: ReservationStatus,
    val actingCitizenId: UUID?,
    val reserverId: UUID?,
    val validity: ReservationValidity,
    val paymentDate: LocalDate?,
    val creationType: CreationType
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
    val actingCitizenId: UUID?,
    val reserverId: UUID?,
    val discountPercentage: Int?,
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
    val locationName: String,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val excludedBoatTypes: List<BoatType>?,
    val validity: ReservationValidity,
    val originalReservationId: Int? = null,
    val creationType: CreationType,
) {
    val discountedPriceCents: Int
        get() = discountedPriceInCents(priceCents, discountPercentage)
    val priceInEuro: String
        get() = formatInt(priceCents)
    val vatPriceInEuro: String
        get() = formatInt(vatCents)
    val priceWithoutVatInEuro: String
        get() = formatInt(netPriceCents)
}

data class BoatSpaceReservationItem(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val reserverId: UUID,
    val actingCitizenId: UUID?,
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
    val invoiceDueDate: LocalDate?,
    val warnings: Set<String> = emptySet(),
    val validity: ReservationValidity?,
    val amenity: BoatSpaceAmenity?
) {
    fun hasWarning(warning: String): Boolean = warnings.contains(warning)

    fun hasAnyWarnings(): Boolean = warnings.isNotEmpty()

    private fun getAmenityForStorageType(): BoatSpaceAmenity =
        when (storageType) {
            null -> BoatSpaceAmenity.None
            StorageType.Trailer -> BoatSpaceAmenity.Trailer
            StorageType.Buck, StorageType.BuckWithTent -> BoatSpaceAmenity.Buck
            else -> BoatSpaceAmenity.None
        }

    fun getBoatSpaceAmenity(): BoatSpaceAmenity =
        when (type) {
            BoatSpaceType.Slip, BoatSpaceType.Storage -> amenity ?: BoatSpaceAmenity.None
            BoatSpaceType.Winter -> getAmenityForStorageType()
            else -> BoatSpaceAmenity.None
        }
}

enum class BoatSpaceFilterColumn {
    PLACE,
    PLACE_TYPE,
    AMENITY,
    PLACE_WIDTH,
    PLACE_LENGTH,
    PRICE,
    ACTIVE,
    RESERVER
}

enum class BoatSpaceReservationFilterColumn {
    START_DATE,
    END_DATE,
    PLACE,
    PLACE_TYPE,
    HOME_TOWN,
    CUSTOMER,
    BOAT,
    EMAIL,
    PHONE,
    AMENITY,
    WARNING_CREATED,
}

enum class PaymentFilter {
    CONFIRMED,
    INVOICED,
    PAYMENT,
    CANCELLED
}

enum class ReservationExpiration {
    Active,
    Expired,
}

enum class BoatSpaceState {
    Active,
    Inactive
}

val sections = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "O")

data class BoatSpaceReservationFilter(
    val sortBy: BoatSpaceReservationFilterColumn = BoatSpaceReservationFilterColumn.PLACE,
    val ascending: Boolean = true,
    val amenity: List<BoatSpaceAmenity> = emptyList(),
    val harbor: List<Int> = emptyList(),
    val payment: List<PaymentFilter> = listOf<PaymentFilter>(PaymentFilter.CONFIRMED, PaymentFilter.INVOICED, PaymentFilter.CANCELLED),
    val nameSearch: String? = null,
    val phoneSearch: String? = null,
    val emailSearch: String? = null,
    val warningFilter: Boolean? = null,
    val exceptionsFilter: Boolean? = null,
    val sectionFilter: List<String> = emptyList(),
    val expiration: ReservationExpiration = ReservationExpiration.Active,
    val boatSpaceType: List<BoatSpaceType> = emptyList(),
    val validity: List<ReservationValidity> = emptyList(),
    val paginationStart: Int = 0,
    val paginationEnd: Int = 50
)

fun ReservationWithDependencies.toBoatSpaceReservation() =
    BoatSpaceReservation(
        id = id,
        boatSpaceId = boatSpaceId,
        startDate = startDate,
        endDate = endDate,
        created = created,
        updated = updated,
        status = status,
        actingCitizenId = actingCitizenId,
        reserverId = reserverId,
        validity = validity ?: ReservationValidity.Indefinite,
        paymentDate = null,
        creationType = creationType
    )

fun BoatSpaceReservation.effectiveEndDate() = if (status == ReservationStatus.Cancelled) endDate.minusDays(1) else endDate
