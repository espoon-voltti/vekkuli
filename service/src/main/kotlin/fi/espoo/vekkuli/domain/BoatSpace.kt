// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

enum class BoatSpaceAmenity {
    None,
    Buoy,
    RearBuoy,
    Beam,
    WalkBeam
}

enum class BoatSpaceType {
    Slip,
    Storage
}

data class BoatSpace(
    val type: BoatSpaceType,
    val section: String,
    val placeNumber: Int,
    val amenity: BoatSpaceAmenity,
    val widthCm: Int,
    val lengthCm: Int,
    val description: String,
    val locationName: String,
    val totalCount: Int,
)

data class BoatSpaceFilter(
    val page: Int,
    val pageSize: Int,
    val minWidth: Int?,
    val maxWidth: Int?,
    val minLength: Int?,
    val maxLength: Int?,
    val amenity: BoatSpaceAmenity?,
    val locationId: Int?,
    val boatSpaceType: BoatSpaceType?,
)

fun Handle.getBoatSpaces(boatSpaceFilter: BoatSpaceFilter): List<BoatSpace> {
    val offset = (boatSpaceFilter.page - 1) * boatSpaceFilter.pageSize
    val sql =
        StringBuilder(
            """
            SELECT boat_space.*, location.name as location_name, location.id as location_id, COUNT(*) OVER() AS total_count
            FROM boat_space
            LEFT JOIN location
            ON location_id = location.id
            WHERE 1=1
            """.trimIndent()
        )

    boatSpaceFilter.minWidth?.let {
        sql.append(" AND width_cm >= :minWidth")
    }
    boatSpaceFilter.maxWidth?.let {
        sql.append(" AND width_cm <= :maxWidth")
    }
    boatSpaceFilter.minLength?.let {
        sql.append(" AND length_cm >= :minLength")
    }
    boatSpaceFilter.maxLength?.let {
        sql.append(" AND length_cm <= :maxLength")
    }
    boatSpaceFilter.locationId?.let {
        sql.append(" AND location_id = :locationId")
    }
    boatSpaceFilter.amenity?.let {
        sql.append(" AND amenity = :amenity")
    }
    boatSpaceFilter.boatSpaceType?.let {
        sql.append(" AND type = :boatSpaceType")
    }
    sql.append(" LIMIT :size OFFSET :offset")

    val query = createQuery(sql.toString())
    boatSpaceFilter.minWidth?.let { query.bind("minWidth", it) }
    boatSpaceFilter.maxWidth?.let { query.bind("maxWidth", it) }
    boatSpaceFilter.minLength?.let { query.bind("minLength", it) }
    boatSpaceFilter.maxLength?.let { query.bind("maxLength", it) }
    boatSpaceFilter.locationId?.let { query.bind("locationId", it) }
    boatSpaceFilter.amenity?.let { query.bind("amenity", it) }
    boatSpaceFilter.boatSpaceType?.let { query.bind("boatSpaceType", it) }
    query.bind("size", boatSpaceFilter.pageSize)
    query.bind("offset", offset)

    return query.mapTo<BoatSpace>().toList()
}
