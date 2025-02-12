package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatSpaceFilter
import fi.espoo.vekkuli.service.BoatSpaceRepository
import fi.espoo.vekkuli.utils.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.core.statement.Query
import org.springframework.stereotype.Repository

fun amenityFilter(
    amenity: BoatSpaceAmenity,
    boatWidth: Int?,
    boatLength: Int?
): SqlExpr {
    val (minWidth, maxWidth) = BoatSpaceConfig.getWidthLimitsForBoatSpace(boatWidth, amenity)
    val (minLength, maxLength) = BoatSpaceConfig.getLengthLimitsForBoatSpace(boatLength, amenity)
    return AndExpr(
        listOf(
            OperatorExpr("amenity", "=", amenity),
            OperatorExpr("width_cm", ">=", minWidth),
            OperatorExpr("width_cm", "<=", maxWidth),
            OperatorExpr("length_cm", ">=", minLength),
            OperatorExpr("length_cm", "<=", maxLength)
        )
    )
}

fun createAmenityFilter(filter: BoatSpaceFilter): SqlExpr {
    if (filter.boatOrSpaceLength != null && filter.boatOrSpaceLength > BoatSpaceConfig.BOAT_LENGTH_THRESHOLD_CM) {
        // Boats over 15 meters will only fit in buoys
        return OperatorExpr(
            "amenity",
            "=",
            BoatSpaceAmenity.Buoy,
        )
    }

    val amenities = if (filter.amenities.isNullOrEmpty()) BoatSpaceAmenity.entries.toList() else filter.amenities
    return OrExpr(
        amenities.map {
            amenityFilter(it, filter.boatOrSpaceWidth, filter.boatOrSpaceLength)
        }
    )
}

class LocationExpr(
    private val locationId: Int,
    private val boatTypeVar: String?
) : SqlExpr() {
    private val name: String = "li_${getNextIndex()}"

    override fun toSql(): String {
        if (boatTypeVar != null) {
            return """
                (location.id = :$name AND 
                :$boatTypeVar NOT IN 
                  (SELECT excluded_boat_type FROM harbor_restriction WHERE location_id = :$name))
                """.trimIndent()
        }
        return "location.id = :$name"
    }

    override fun bind(query: Query) {
        query.bind(name, locationId)
    }
}

class LocationFilter(
    locationIds: List<Int>,
    private val boatType: BoatType?
) : SqlExpr() {
    private val boatTypeVar: String? = if (boatType != null) "bs_${getNextIndex()}" else null
    private val expr =
        if (locationIds.isNotEmpty()) {
            OrExpr(locationIds.map { LocationExpr(it, boatTypeVar) })
        } else {
            EmptyExpr()
        }

    override fun toSql(): String = expr.toSql()

    override fun bind(query: Query) {
        if (boatType !== null && boatTypeVar !== null) {
            query.bind(boatTypeVar, boatType)
        }
        expr.bind(query)
    }
}

@Repository
class JdbiBoatSpaceRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) : BoatSpaceRepository {
    override fun getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int> {
        return jdbi.withHandleUnchecked { handle ->
            if (params.boatOrSpaceWidth == null || params.boatOrSpaceLength == null) return@withHandleUnchecked Pair(emptyList<Harbor>(), 0)
            val amenityFilter = createAmenityFilter(params)
            val locationIds =
                if (params.locationIds.isNullOrEmpty()) {
                    handle
                        .createQuery(
                            "SELECT id FROM location"
                        ).mapTo<Int>()
                        .toList()
                } else {
                    params.locationIds
                }
            val locationFilter = LocationFilter(locationIds, params.boatType)
            val boatSpaceTypeFilter = OperatorExpr("type", "=", params.boatSpaceType)
            val combinedFilter =
                AndExpr(
                    listOf(
                        amenityFilter,
                        locationFilter,
                        boatSpaceTypeFilter
                    )
                )

            val sql =
                """
                SELECT 
                    location.name as location_name, 
                    location.address as location_address,
                    boat_space.id,
                    CONCAT(section, ' ', TO_CHAR(place_number, 'FM000')) as place,
                    length_cm, 
                    width_cm, 
                    price.price_cents,
                    amenity
                FROM boat_space
                JOIN location
                ON location_id = location.id
                JOIN price
                ON price_id = price.id
                LEFT JOIN boat_space_reservation
                ON boat_space.id = boat_space_reservation.boat_space_id
                AND (
                    (boat_space_reservation.created <= :currentTime) AND
                    (boat_space_reservation.status IN ('Info', 'Payment') AND boat_space_reservation.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)) OR
                    (boat_space_reservation.status IN ('Confirmed', 'Invoiced') AND boat_space_reservation.end_date::date >= :currentTime::date) OR
                    (boat_space_reservation.status = 'Cancelled' AND boat_space_reservation.end_date::date > :currentTime::date)
                )
                WHERE 
                    boat_space_reservation.id IS NULL
                    AND ${combinedFilter.toSql()}
                    
                ORDER BY width_cm, length_cm, section, place_number
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)

            combinedFilter.bind(query)

            val boatSpaces = query.mapTo<BoatSpaceOption>().toList()
            val count = boatSpaces.size

            val harbors =
                boatSpaces
                    .groupBy { it.locationName }
                    .map { (locationName, spaces) ->
                        Harbor(
                            location =
                                Location(
                                    id = spaces.first().id,
                                    name = locationName,
                                    address = spaces.first().locationAddress
                                ),
                            boatSpaces = spaces
                        )
                    }.sortedBy { it.location.name }

            Pair(harbors, count)
        }
    }

    override fun getBoatSpace(boatSpaceId: Int): BoatSpace? =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                ${buildBoatSpaceSelector()}
                WHERE bs.id = :boatSpaceId
                GROUP BY bs.id, location.name, location.address
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bind("boatSpaceId", boatSpaceId)

            query.mapTo<BoatSpace>().firstOrNull()
        }

    override fun getBoatSpaces(): List<BoatSpace> =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                ${buildBoatSpaceSelector()}
                GROUP BY bs.id, location.name
                """.trimIndent()

            handle.createQuery(sql).mapTo<BoatSpace>().toList()
        }

    private fun buildBoatSpaceSelector() =
        """SELECT 
                    bs.*,
                    location.name as location_name, 
                    location.address as location_address,
                    ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                FROM boat_space bs
                JOIN location ON bs.location_id = location.id
                JOIN price ON bs.price_id = price.id
                LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id"""

    override fun isBoatSpaceReserved(boatSpaceId: Int): Boolean =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                SELECT EXISTS (
                SELECT 1
                FROM boat_space_reservation bsr
                WHERE bsr.boat_space_id = :boatSpaceId
                  AND (((bsr.status = 'Confirmed' OR bsr.status = 'Invoiced') AND bsr.end_date >= :endDateCut)
                       OR (bsr.status = 'Cancelled' AND bsr.end_date > :endDateCut))
                )
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("endDateCut", timeProvider.getCurrentDate())

            query.mapTo<Boolean>().first()
        }
}
