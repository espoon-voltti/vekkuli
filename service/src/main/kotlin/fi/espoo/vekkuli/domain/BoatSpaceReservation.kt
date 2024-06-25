// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later
package fi.espoo.vekkuli.domain

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

data class BoatSpaceReservationWithCitizen(
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
    val phone: String
)

fun Handle.getReservationWithCitizen(id: Int): BoatSpaceReservationWithCitizen? {
    val query =
        createQuery(
            """
            SELECT bsr.*, c.first_name, c.last_name, c.email, c.phone
            FROM boat_space_reservation bsr
            JOIN citizen c ON bsr.citizen_id = c.id 
            WHERE bsr.id = :id
                AND bsr.status = 'Info' 
                AND bsr.created > NOW() - INTERVAL '30 minutes'
            """.trimIndent()
        )
    query.bind("id", id)
    return query.mapTo<BoatSpaceReservationWithCitizen>().findOne().orElse(null)
}
