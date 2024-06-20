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
    Storage,
    Trailer
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

data class Harbor(val location: Location, val boatSpaceGroups: List<BoatSpaceGroup>)

data class BoatSpaceGroup(
    val locationName: String,
    val section: String,
    val length_cm: Int,
    val width_cm: Int,
    val count: Int,
    val amenity: BoatSpaceAmenity,
    val price: Int,
    val type: BoatSpaceType
)

fun Handle.getHarbors(
    width: Int? = null,
    length: Int? = null,
    amenities: List<BoatSpaceAmenity>? = null,
    boatSpaceType: BoatSpaceType? = null
): List<Harbor> {
    val sql =
        """
        SELECT location.id as location_id, location.name as location_name, section, length_cm, width_cm, COUNT(*) as count, amenity, price.price as price, boat_space.type as type 
        FROM boat_space
        JOIN location
        ON location_id = location.id
        JOIN price
        ON price_id = price.id
        WHERE 1=1
            ${if (width != null) "AND width_cm >= :minWidth AND width_cm <= :maxWidth" else ""}
            ${if (length != null) "AND length_cm >= :minLength AND length_cm <= :maxLength" else ""}
            ${if (!amenities.isNullOrEmpty()) "AND amenity IN (<amenities>)" else ""}
            ${if (boatSpaceType != null) "AND type = :boatSpaceType" else ""}
        GROUP BY location.id, location.name, section, length_cm, width_cm, amenity, price
        ORDER BY price 
        """.trimIndent()

    val query = createQuery(sql)
    if (width != null) {
        query.bind("minWidth", width - 50)
        query.bind("maxWidth", width + 50)
    }
    if (length != null) {
        query.bind("minLength", length - 50)
        query.bind("maxLength", length + 50)
    }
    if (!amenities.isNullOrEmpty()) {
        query.bindList("amenities", amenities)
    }
    if (boatSpaceType != null) {
        query.bind("boatSpaceType", boatSpaceType)
    }

    val boatSpaceGroups =
        query.map { rs, _ ->
            val location =
                Location(
                    id = rs.getInt("location_id"),
                    name = rs.getString("location_name"),
                    address = ""
                )
            val boatSpaceGroup =
                BoatSpaceGroup(
                    locationName = rs.getString("location_name"),
                    section = rs.getString("section"),
                    length_cm = rs.getInt("length_cm"),
                    width_cm = rs.getInt("width_cm"),
                    count = rs.getInt("count"),
                    amenity = BoatSpaceAmenity.valueOf(rs.getString("amenity")),
                    price = rs.getInt("price"),
                    type = BoatSpaceType.valueOf(rs.getString("type"))
                )
            Pair(location, boatSpaceGroup)
        }.toList()

    val harbors =
        boatSpaceGroups
            .groupBy { it.first }
            .map { (location, groups) ->
                Harbor(location, groups.map { it.second })
            }

    return harbors
}

fun Handle.getUnreservedBoatSpaceOptions(
    width: Int? = null,
    length: Int? = null,
    amenities: List<BoatSpaceAmenity>? = null,
    boatSpaceType: BoatSpaceType? = null,
    locationIds: List<Int>? = null
): List<Harbor> {
    val sql =
        """
        SELECT location.id as location_id,type , location.name as location_name, section, length_cm, width_cm, COUNT(*) as count, amenity, price.price as price
        FROM boat_space
        JOIN location
        ON location_id = location.id
        JOIN price
        ON price_id = price.id
        WHERE 1=1
            ${if (width != null) "AND width_cm >= :minWidth AND width_cm <= :maxWidth" else ""}
            ${if (length != null) "AND length_cm >= :minLength AND length_cm <= :maxLength" else ""}
            ${if (!amenities.isNullOrEmpty()) "AND amenity IN (<amenities>)" else ""}
            ${if (boatSpaceType != null) "AND type = :boatSpaceType" else ""}
            ${if (!locationIds.isNullOrEmpty()) "AND location.id IN (<locationIds>)" else ""}
        GROUP BY location.id, location.name, type, section, length_cm, width_cm, amenity, price
        ORDER BY price 
        """.trimIndent()

    val query = createQuery(sql)
    if (width != null) {
        query.bind("minWidth", width - 50)
        query.bind("maxWidth", width + 50)
    }
    if (length != null) {
        query.bind("minLength", length - 50)
        query.bind("maxLength", length + 50)
    }
    if (!amenities.isNullOrEmpty()) {
        query.bindList("amenities", amenities)
    }
    if (boatSpaceType != null) {
        query.bind("boatSpaceType", boatSpaceType)
    }
    if (!locationIds.isNullOrEmpty()) {
        query.bindList("locationIds", locationIds)
    }

    val boatSpaceGroups =
        query.map { rs, _ ->
            val location =
                Location(
                    id = rs.getInt("location_id"),
                    name = rs.getString("location_name"),
                    address = ""
                )
            val boatSpaceGroup =
                BoatSpaceGroup(
                    locationName = rs.getString("location_name"),
                    section = rs.getString("section"),
                    length_cm = rs.getInt("length_cm"),
                    width_cm = rs.getInt("width_cm"),
                    count = rs.getInt("count"),
                    amenity = BoatSpaceAmenity.valueOf(rs.getString("amenity")),
                    price = rs.getInt("price"),
                    type = BoatSpaceType.valueOf(rs.getString("type"))
                )
            Pair(location, boatSpaceGroup)
        }.toList()

    val harbors =
        boatSpaceGroups
            .groupBy { it.first }
            .map { (location, groups) ->
                Harbor(location, groups.map { it.second })
            }

    return harbors
}

fun Handle.getUnreservedBoatSpace(
    width: Int,
    length: Int,
    amenity: BoatSpaceAmenity,
    boatSpaceType: BoatSpaceType,
    section: String
): BoatSpace? {
    val sql =
        """
        SELECT boat_space.*, location.name as location_name, location.id as location_id, COUNT(*) OVER() AS total_count
        FROM boat_space
        LEFT JOIN location
        ON location_id = location.id
        WHERE 1=1
            AND width_cm = :width
            AND length_cm = :length
            AND amenity = :amenity
            AND type = :boatSpaceType
            AND section = :section
        """.trimIndent()
    val query = createQuery(sql)
    query.bind("width", width)
    query.bind("length", length)
    query.bind("amenity", amenity)
    query.bind("boatSpaceType", boatSpaceType)
    query.bind("section", section)

    return query.mapTo<BoatSpace>().first()
}
