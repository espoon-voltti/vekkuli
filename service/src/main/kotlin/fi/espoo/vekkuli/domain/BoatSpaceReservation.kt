// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.InExpr
import fi.espoo.vekkuli.utils.cmToM
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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
) {
    companion object {
        fun getReservationsForCitizen(
            id: UUID,
            jdbi: Jdbi
        ): List<BoatSpaceReservationDetails> {
            return jdbi.inTransactionUnchecked { tx ->
                tx.getBoatSpaceReservationsForCitizen(id)
            }
        }
    }
}

fun Handle.insertBoatSpaceReservation(
    citizenId: UUID,
    boatSpaceId: Int,
    startDate: LocalDate,
    endDate: LocalDate,
    status: ReservationStatus,
): BoatSpaceReservation {
    val query =
        createQuery(
            """
            INSERT INTO boat_space_reservation (citizen_id, boat_space_id, start_date, end_date, status)
            VALUES (:citizenId, :boatSpaceId, :startDate, :endDate, :status)
            RETURNING *
            """.trimIndent()
        )
    query.bind("citizenId", citizenId)
    query.bind("boatSpaceId", boatSpaceId)
    query.bind("startDate", startDate)
    query.bind("endDate", endDate)
    query.bind("status", status)
    return query.mapTo<BoatSpaceReservation>().one()
}

fun Handle.updateBoatInBoatSpaceReservation(
    reservationId: Int,
    boatId: Int,
): BoatSpaceReservation {
    val query =
        createQuery(
            """
            UPDATE boat_space_reservation
            SET status = 'Payment', updated = :updatedTime, boat_id = :boatId
            WHERE id = :id
                AND status = 'Info' 
                AND created > NOW() - make_interval(secs => :sessionTimeInSeconds)
            RETURNING *
            """.trimIndent()
        )
    query.bind("updatedTime", LocalDate.now())
    query.bind("id", reservationId)
    query.bind("boatId", boatId)
    query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)

    return query.mapTo<BoatSpaceReservation>().one()
}

fun Handle.updateReservationWithPayment(
    reservationId: Int,
    paymentId: UUID,
): BoatSpaceReservation {
    val query =
        createQuery(
            """
            UPDATE boat_space_reservation
            SET status = 'Payment', updated = :updatedTime, payment_id = :paymentId
            WHERE id = :id
                AND status = 'Payment'
                AND created > NOW() - make_interval(secs => :paymentTimeout)
            RETURNING *
            """.trimIndent()
        )
    query.bind("updatedTime", LocalDate.now())
    query.bind("id", reservationId)
    query.bind("paymentId", paymentId)
    query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)

    return query.mapTo<BoatSpaceReservation>().one()
}

data class ReservationWithDependencies(
    val id: Int,
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
    val price: Int,
)

fun Handle.getReservationForCitizen(id: UUID): ReservationWithDependencies? {
    val query =
        createQuery(
            """
            SELECT bsr.*, c.first_name, c.last_name, c.email, c.phone, 
                location.name as location_name, price.price as price, 
                bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                  bs.description
            FROM boat_space_reservation bsr
            JOIN citizen c ON bsr.citizen_id = c.id 
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN price ON price_id = price.id
            WHERE bsr.citizen_id = :id
                AND bsr.status = 'Info' 
                AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
            """.trimIndent()
        )
    query.bind("id", id)
    query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
    return query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
}

fun Handle.removeBoatSpaceReservation(
    id: Int,
    citizenId: UUID,
) {
    val query =
        createUpdate(
            """
            DELETE FROM boat_space_reservation
            WHERE id = :id AND citizen_id = :citizenId
            """.trimIndent()
        )
    query.bind("id", id)
    query.bind("citizenId", citizenId)
    query.execute()
}

fun Handle.updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): String? {
    val query =
        createQuery(
            """
            UPDATE boat_space_reservation
            SET status = 'Confirmed', updated = :updatedTime
            WHERE payment_id = :paymentId
                AND status = 'Payment' 
                AND created > NOW() - make_interval(secs => :paymentTimeout)
            RETURNING id
            """.trimIndent()
        )
    query.bind("paymentId", paymentId)
    query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
    query.bind("updatedTime", LocalDate.now())
    return query.mapTo<String>().findOne().orElse(null)
}

fun Handle.getBoatSpaceReservationIdForPayment(paymentId: UUID): String? {
    val query =
        createQuery(
            """
            SELECT id
            FROM boat_space_reservation
            WHERE payment_id = :paymentId
                AND status = 'Payment' 
                AND created > NOW() - make_interval(secs => :paymentTimeout)
            """.trimIndent()
        )
    query.bind("paymentId", paymentId)
    query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
    return query.mapTo<String>().findOne().orElse(null)
}

fun Handle.getReservationWithCitizen(id: Int): ReservationWithDependencies? {
    val query =
        createQuery(
            """
            SELECT bsr.*, c.first_name, c.last_name, c.email, c.phone, 
                location.name as location_name, price.price as price, 
                bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                  bs.description
            FROM boat_space_reservation bsr
            JOIN citizen c ON bsr.citizen_id = c.id 
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN price ON price_id = price.id
            WHERE bsr.id = :id
                AND bsr.status = 'Info' 
                AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
            """.trimIndent()
        )
    query.bind("id", id)
    query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
    return query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
}

data class BoatSpaceReservationDetails(
    val id: Int,
    val price: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val homeTown: String,
    val email: String,
    val phone: String,
    val address: String?,
    val postalCode: String?,
    val municipality: String?,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val registrationCode: String?,
    val boatOwnership: OwnershipStatus?,
    val boatRegistrationCode: String?,
    val boatName: String?,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatDepthCm: Int,
    val boatType: BoatType,
    val boatOtherIdentification: String?,
    val boatExtraInformation: String?,
    val boatSpaceLengthCm: Int,
    val boatSpaceWidthCm: Int,
    val amenity: BoatSpaceAmenity,
    val validity: ReservationValidity? = ReservationValidity.ValidUntilFurtherNotice
) {
    val boatLengthInM: Double
        get() = boatLengthCm.cmToM()
    val boatWidthInM: Double
        get() = boatWidthCm.cmToM()
    val boatDepthInM: Double
        get() = boatDepthCm.cmToM()
    val boatSpaceLengthInM: Double
        get() = boatSpaceLengthCm.cmToM()
    val boatSpaceWidthInM: Double
        get() = boatSpaceWidthCm.cmToM()
    val alvPriceInEuro: Int
        get() = (price * (BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE / 100)).toInt()
    val priceWithoutAlvInEuro: Int
        get() = price - alvPriceInEuro
    val showOwnershipWarning: Boolean
        get() = boatOwnership == OwnershipStatus.FutureOwner || boatOwnership == OwnershipStatus.CoOwner
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
) {
    val showOwnershipWarning: Boolean
        get() = boatOwnership == OwnershipStatus.FutureOwner || boatOwnership == OwnershipStatus.CoOwner
}

enum class BoatSpaceFilterColumn {
    START_DATE,
    END_DATE,
    PLACE,
    CUSTOMER,
}

data class BoatSpaceReservationFilter(
    val sortBy: BoatSpaceFilterColumn = BoatSpaceFilterColumn.START_DATE,
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

fun getSortingSql(sort: BoatSpaceReservationFilter): String =
    when (sort.sortBy) {
        BoatSpaceFilterColumn.START_DATE -> "ORDER BY start_date"
        BoatSpaceFilterColumn.END_DATE -> "ORDER BY end_date"
        BoatSpaceFilterColumn.PLACE -> "ORDER BY place"
        BoatSpaceFilterColumn.CUSTOMER -> "ORDER BY full_name"
    } + if (!sort.ascending) " DESC" else ""

fun Handle.getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem> {
    val filter =
        AndExpr(
            listOf(
                InExpr("bs.location_id", params.harbor),
                InExpr("bs.amenity", params.amenity) { "'$it'" },
            )
        )

    val query =
        createQuery(
            """
            SELECT bsr.*, CONCAT(c.last_name, ' ', c.first_name) as full_name, c.first_name, c.last_name, c.email, c.phone, '' as home_town,
                b.registration_code as boat_registration_code,
                b.ownership as boat_ownership,
                location.name as location_name, 
                bs.type, CONCAT(bs.section, bs.place_number) as place
            FROM boat_space_reservation bsr
            JOIN boat b on b.id = bsr.boat_id
            JOIN citizen c ON bsr.citizen_id = c.id 
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            WHERE
              bsr.status = 'Confirmed'
            AND ${filter.toSql().ifBlank { "true" }}
            ${getSortingSql(params)}
            """.trimIndent()
        )

    filter.bind(query)
    return query.mapTo<BoatSpaceReservationItem>().list()
}

fun Handle.getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails {
    val query =
        createQuery(
            """
            SELECT bsr.id,
                   bsr.start_date,
                   bsr.end_date,
                   bsr.created,
                   bsr.updated,
                   bsr.status,
                   bsr.boat_space_id,
                   CONCAT(c.last_name, ' ', c.first_name) as full_name, 
                   c.first_name, 
                   c.last_name, 
                   c.email, 
                   c.phone,
                   c.national_id,
                   c.address,
                   c.postal_code,
                   c.municipality,
                   '' as home_town,
                   b.registration_code as boat_registration_code,
                   b.ownership as boat_ownership,
                   b.id as boat_id,
                   b.name as boat_name,
                   b.width_cm as boat_width_cm,
                   b.length_cm as boat_length_cm,
                   b.weight_kg as boat_weight_kg,
                   b.depth_cm as boat_depth_cm,
                   b.type as boat_type,
                   b.other_identification as boat_other_identification,
                   b.extra_information as boat_extra_information,
                   location.name as location_name, 
                   bs.type,
                    bs.length_cm as boat_space_length_cm,
                    bs.width_cm as boat_space_width_cm,
                    bs.amenity,
                   price.price as price,
                   CONCAT(bs.section, bs.place_number) as place
            FROM boat_space_reservation bsr
            JOIN boat b ON b.id = bsr.boat_id
            JOIN citizen c ON bsr.citizen_id = c.id 
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location.id = bs.location_id
            JOIN price ON price_id = price.id
            WHERE bsr.id = :reservationId
            """.trimIndent()
        )
    query.bind("reservationId", reservationId)
    return query.mapTo<BoatSpaceReservationDetails>().findOne().orElse(null)
}

fun Handle.getBoatSpaceReservationsForCitizen(citizenId: UUID): List<BoatSpaceReservationDetails> {
    val query =
        createQuery(
            """
            SELECT bsr.id,
                   bsr.start_date,
                   bsr.end_date,
                   bsr.created,
                   bsr.updated,
                   bsr.status,
                   bsr.boat_space_id,
                   CONCAT(c.last_name, ' ', c.first_name) as full_name, 
                   c.first_name, 
                   c.last_name, 
                   c.email, 
                   c.phone,
                   c.national_id,
                   c.address,
                   c.postal_code,
                   c.municipality,
                   '' as home_town,
                   b.registration_code as boat_registration_code,
                   b.ownership as boat_ownership,
                   b.id as boat_id,
                   b.name as boat_name,
                   b.width_cm as boat_width_cm,
                   b.length_cm as boat_length_cm,
                   b.weight_kg as boat_weight_kg,
                   b.depth_cm as boat_depth_cm,
                   b.type as boat_type,
                   b.other_identification as boat_other_identification,
                   b.extra_information as boat_extra_information,
                   location.name as location_name, 
                   bs.type,
                    bs.length_cm as boat_space_length_cm,
                    bs.width_cm as boat_space_width_cm,
                    bs.amenity,
                   price.price as price,
                   CONCAT(bs.section, bs.place_number) as place
            FROM boat_space_reservation bsr
            JOIN boat b ON b.id = bsr.boat_id
            JOIN citizen c ON bsr.citizen_id = c.id 
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location.id = bs.location_id
            JOIN price ON price_id = price.id
            WHERE c.id = :citizenId AND bsr.status = 'Confirmed'
            """.trimIndent()
        )
    query.bind("citizenId", citizenId)
    return query.mapTo<BoatSpaceReservationDetails>().list()
}
