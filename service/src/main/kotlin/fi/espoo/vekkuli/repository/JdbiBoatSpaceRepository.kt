package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.boatSpaceDetails.BoatSpaceHistory
import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceListRow
import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceSortBy
import fi.espoo.vekkuli.boatSpace.boatSpaceList.BoatSpaceStats
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatSpaceFilter
import fi.espoo.vekkuli.service.BoatSpaceRepository
import fi.espoo.vekkuli.service.CreateBoatSpaceParams
import fi.espoo.vekkuli.service.EditBoatSpaceParams
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
                    location.id as location_id,
                    boat_space.id,
                    CONCAT(section, ' ', TO_CHAR(place_number, 'FM000')) as place,
                    length_cm, 
                    width_cm, 
                    price.price_cents,
                    amenity
                FROM boat_space
                JOIN location
                ON boat_space.location_id = location.id
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
                    AND boat_space.is_active = TRUE
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
                                    id = spaces.first().locationId,
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
                SELECT 
                     bs.*,
                     location.name as location_name, 
                     location.address as location_address,
                     ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                 FROM boat_space bs
                 JOIN location ON bs.location_id = location.id
                 JOIN price ON bs.price_id = price.id
                 LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                 WHERE bs.id = :boatSpaceId
                 GROUP BY bs.id, location.name, location.address
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bind("boatSpaceId", boatSpaceId)

            query.mapTo<BoatSpace>().firstOrNull()
        }

    override fun checkIfAnyBoatSpacesHaveReservations(boatSpaceIds: List<Int>): Boolean? =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                SELECT EXISTS (
                    SELECT 1
                    FROM boat_space_reservation
                    WHERE boat_space_id IN (<boatSpaceIds>)
                )
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bindList("boatSpaceIds", boatSpaceIds)

            query.mapTo<Boolean>().firstOrNull()
        }

    override fun getBoatSpaces(
        filter: SqlExpr,
        sortBy: BoatSpaceSortBy?,
        pagination: PaginationExpr?,
    ): List<BoatSpaceListRow> =
        jdbi.withHandleUnchecked { handle ->
            val sortByQuery = sortBy?.apply()?.takeIf { it.isNotEmpty() } ?: ""
            val paginationQuery = pagination?.toSql() ?: ""
            val filterQuery = if (filter.toSql().isNotEmpty()) """WHERE ${filter.toSql()}""" else ""
            val sql =
                """
                SELECT 
                    bs.id,
                    bs.type,
                    bs.amenity,
                    bs.width_cm,
                    bs.length_cm,
                    bs.is_active,
                    location.name AS location_name, 
                    location.address AS location_address,
                    price.price_cents,
                    price.name as price_class,
                    r.name as reserver_name,
                    r.id as reserver_id,
                    CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                    r.type as reserver_type
                ${buildBoatSpacePickQuery()}
                $filterQuery
                    GROUP BY bs.id, location.name, location.address,
                        price.price_cents, price.name,
                        r.name, r.id
                $sortByQuery
                $paginationQuery
                """.trimIndent()

            val query = handle.createQuery(sql)
            filter.bind(query)
            pagination?.bind(query)
            query.bind("endDateCut", timeProvider.getCurrentDate())

            query.mapTo<BoatSpaceListRow>().toList()
        }

    private fun buildBoatSpacePickQuery(): String =
        (
            """FROM boat_space bs    
                    JOIN location ON bs.location_id = location.id
                    JOIN price ON bs.price_id = price.id
                    LEFT JOIN (
                        SELECT id, boat_space_id, reserver_id, storage_type
                        FROM boat_space_reservation
                        WHERE 
                            (status IN ('Confirmed', 'Invoiced') AND end_date >= :endDateCut)
                            OR (status = 'Cancelled' AND end_date > :endDateCut)
                    ) bsr ON bsr.boat_space_id = bs.id
                    LEFT JOIN reserver r ON r.id = bsr.reserver_id
                    """
        )

    override fun getSections(): List<String> =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                SELECT DISTINCT section
                FROM boat_space
                ORDER BY section
                """.trimIndent()

            handle.createQuery(sql).mapTo<String>().toList()
        }

    override fun editBoatSpaces(
        boatSpaceIds: List<Int>,
        editBoatSpaceParams: EditBoatSpaceParams
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                UPDATE boat_space bs
                SET type = COALESCE(:type, type),
                    location_id = COALESCE(:locationId, location_id),
                    section = COALESCE(:section, section),
                    place_number = COALESCE(:placeNumber, place_number),
                    amenity = COALESCE(:amenity, amenity),
                    width_cm = COALESCE(:widthCm, width_cm),
                    length_cm = COALESCE(:lengthCm, length_cm),
                    price_id = COALESCE(:priceId, price_id),
                    is_active = COALESCE(:isActive, is_active),
                    updated = :currentTime
                WHERE bs.id IN (<boatSpaceIds>)
                """.trimIndent()
            val query = handle.createUpdate(sql)
            query.bindList("boatSpaceIds", boatSpaceIds)
            query.bind("type", editBoatSpaceParams.type)
            query.bind("section", editBoatSpaceParams.section)
            query.bind("placeNumber", editBoatSpaceParams.placeNumber)
            query.bind("locationId", editBoatSpaceParams.locationId)
            query.bind("amenity", editBoatSpaceParams.amenity)
            query.bind("widthCm", editBoatSpaceParams.widthCm)
            query.bind("lengthCm", editBoatSpaceParams.lengthCm)
            query.bind("priceId", editBoatSpaceParams.priceId)
            query.bind("isActive", editBoatSpaceParams.isActive)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.execute()
        }

    override fun getBoatSpaceCount(filter: SqlExpr): BoatSpaceStats =
        jdbi.withHandleUnchecked { handle ->
            val filterQuery = if (filter.toSql().isNotEmpty()) """WHERE ${filter.toSql()}""" else ""

            val sql =
                """
                SELECT COUNT(DISTINCT bs.id) as spaces, COUNT(DISTINCT bsr.id) as reservations
                ${buildBoatSpacePickQuery()}
                $filterQuery
                
                """.trimIndent()

            val query = handle.createQuery(sql)
            filter.bind(query)
            query.bind("endDateCut", timeProvider.getCurrentDate())

            query.mapTo<BoatSpaceStats>().first()
        }

    override fun deleteBoatSpaces(boatSpaceIds: List<Int>) {
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                DELETE FROM boat_space
                WHERE id IN (<boatSpaceIds>)
                """.trimIndent()
            val query = handle.createUpdate(sql)
            query.bindList("boatSpaceIds", boatSpaceIds)
            query.execute()
        }
    }

    override fun createBoatSpace(params: CreateBoatSpaceParams): Int =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                INSERT INTO boat_space (
                    type,
                    location_id,
                    price_id,
                    section,
                    place_number,
                    amenity,
                    width_cm,
                    length_cm,
                    created,
                    updated,
                    is_active
                ) VALUES (
                    :type,
                    :locationId,
                    :priceId,
                    :section,
                    :placeNumber,
                    :amenity,
                    :widthCm,
                    :lengthCm,
                    :currentTime,
                    :currentTime,
                    :isActive
                )
                """.trimIndent()
            val query = handle.createUpdate(sql)
            query.bind("type", params.type)
            query.bind("locationId", params.locationId)
            query.bind("priceId", params.priceId)
            query.bind("section", params.section.uppercase())
            query.bind("placeNumber", params.placeNumber)
            query.bind("amenity", params.amenity)
            query.bind("widthCm", params.widthCm)
            query.bind("lengthCm", params.lengthCm)
            query.bind("isActive", params.isActive)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java) // Assuming the ID is of type Long
                .one()
        }

    override fun getBoatSpaceHistory(boatSpaceId: Int): List<BoatSpaceHistory> =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                SELECT r.id as reserver_id,
                    r.name as reserver_name,
                    r.phone as reserver_phone_number,
                    r.email as reserver_email_address,
                    r.type as reserver_type,
                    r.email as reserver_email,
                    bsr.end_date as reservation_end_date,
                    bsr.created as reservation_create_date,
                    bsr.status as reservation_status,
                    b.registration_code as boat_registration_number,
                    b.name as boat_name
                    FROM boat_space_reservation bsr
                    JOIN reserver r ON r.id = bsr.reserver_id
                    JOIN boat b ON b.id = bsr.boat_id
                    WHERE bsr.boat_space_id = :boatSpaceId 
                        AND (bsr.status IN ('Confirmed', 'Invoiced','Cancelled'))
                    ORDER BY bsr.end_date DESC
                """.trimIndent()
            val query = handle.createQuery(sql)
            query.bind("boatSpaceId", boatSpaceId)
            query.mapTo<BoatSpaceHistory>().toList()
        }

    override fun getBoatWidthOptions(filter: SqlExpr): List<Int> =
        jdbi.withHandleUnchecked { handle ->
            val filterQuery = if (filter.toSql().isNotEmpty()) """WHERE ${filter.toSql()}""" else ""

            val sql =
                """
                SELECT DISTINCT bs.width_cm
                ${buildBoatSpacePickQuery()}
                $filterQuery
                ORDER BY bs.width_cm ASC
                """.trimIndent()
            val query = handle.createQuery(sql)
            filter.bind(query)
            query.bind("endDateCut", timeProvider.getCurrentDate())
            query.mapTo<Int>().toList()
        }

    override fun getBoatLengthOptions(filter: SqlExpr): List<Int> =
        jdbi.withHandleUnchecked { handle ->
            val filterQuery = if (filter.toSql().isNotEmpty()) """WHERE ${filter.toSql()}""" else ""

            val sql =
                """
                SELECT DISTINCT bs.length_cm
                 ${buildBoatSpacePickQuery()}
                $filterQuery
                 ORDER BY bs.length_cm ASC
                """.trimIndent()
            val query = handle.createQuery(sql)
            filter.bind(query)
            query.bind("endDateCut", timeProvider.getCurrentDate())
            query.mapTo<Int>().toList()
        }

    override fun isBoatSpaceAvailable(boatSpaceId: Int): Boolean =
        jdbi.withHandleUnchecked { handle ->
            val sql =
                """
                SELECT NOT EXISTS (
                    SELECT 1
                    FROM boat_space_reservation bsr
                    WHERE bsr.boat_space_id = :boatSpaceId 
                        AND (
                            (bsr.status IN ('Confirmed', 'Invoiced') AND bsr.end_date >= :endDateCut)
                            OR
                            (bsr.status = 'Cancelled' AND bsr.end_date > :endDateCut)
                            OR
                            (bsr.status = 'Info' AND (bsr.created > :currentTime - make_interval(secs => :paymentTimeout)))
                        )
                ) AND EXISTS (
                    SELECT 1 FROM boat_space bs WHERE bs.id = :boatSpaceId AND bs.is_active = TRUE
                )
                """.trimIndent()

            val query = handle.createQuery(sql)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("endDateCut", timeProvider.getCurrentDate())
            query.bind("paymentTimeout", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())

            query.mapTo<Boolean>().first()
        }
}
