// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.BoatSpaceConfig
import org.jdbi.v3.core.Handle
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
)

fun Handle.insertBoatSpaceReservation(
    citizenId: UUID,
    boatSpaceId: Int,
    startDate: LocalDate,
    endDate: LocalDate,
    status: ReservationStatus
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

fun Handle.updateBoatSpaceReservation(
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
    val price: Int
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
    citizenId: UUID
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

data class BoatSpaceReservationItem(
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
    val boatOwnership: OwnershipStatus?
)

enum class BoatSpaceFilterColumn {
    START_DATE,
    END_DATE,
    PLACE,
    CUSTOMER,
}

fun getSortingSql(sort: BoatSpaceSort): String =
    when (sort.column) {
        BoatSpaceFilterColumn.START_DATE -> "ORDER BY start_date"
        BoatSpaceFilterColumn.END_DATE -> "ORDER BY end_date"
        BoatSpaceFilterColumn.PLACE -> "ORDER BY place"
        BoatSpaceFilterColumn.CUSTOMER -> "ORDER BY full_name"
    } + if (!sort.ascending) " DESC" else ""

data class BoatSpaceSort(
    val column: BoatSpaceFilterColumn,
    val ascending: Boolean,
)

fun Handle.getBoatSpaceReservations(sort: BoatSpaceSort): List<BoatSpaceReservationItem> {
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
              bsr.status != 'Info' 
                OR
              bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
            ${getSortingSql(sort)}
            """.trimIndent()
        )
    query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
    return query.mapTo<BoatSpaceReservationItem>().list()
}
