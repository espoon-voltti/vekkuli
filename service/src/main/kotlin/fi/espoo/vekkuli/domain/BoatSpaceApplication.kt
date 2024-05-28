package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.common.BoatSpaceApplicationRowMapper
import org.jdbi.v3.core.Handle


enum class BoatType {
    Rowboat,
    OutboardMotor,
    InboardMotor,
    Sailboat,
    JetSki
}

data class LocationWish(
    val locationId: Int,
    val priority: Int,
)

data class BoatSpaceApplication(
    val createdAt: String,
    val type: BoatSpaceType,
    val boatType: BoatType,
    val amenity: BoatSpaceAmenity,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatRegistrationCode: String,
    val information: String,
    val locationWishes: List<LocationWish>,
    val totalCount: Int,
    )

data class BoatSpaceApplicationFilter(
    val page: Int,
    val pageSize: Int,
)

fun Handle.getBoatSpaceApplications(filter: BoatSpaceApplicationFilter): List<BoatSpaceApplication> {
    val offset = (filter.page - 1) * filter.pageSize
    val sql = StringBuilder("""
        SELECT
            bsa.*, COUNT(*) OVER() AS total_count,
            COALESCE(
                JSON_AGG(
                    JSON_BUILD_OBJECT(
                        'location_id', bsalw.location_id,
                        'priority', bsalw.priority
                    )
                ) FILTER (WHERE bsalw.boat_space_application_id IS NOT NULL), '[]'
            ) AS location_wishes
        FROM
            boat_space_application bsa
        LEFT JOIN boat_space_application_location_wish bsalw
        ON bsa.id = bsalw.boat_space_application_id
        GROUP BY
            bsa.id
    """.trimIndent())

    sql.append(" LIMIT :size OFFSET :offset")

    val query = createQuery(sql.toString())

    query.bind("size", filter.pageSize)
    query.bind("offset", offset)
    return query.map(BoatSpaceApplicationRowMapper())
        .toList()
}