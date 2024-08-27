package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.config.Dimensions
import fi.espoo.vekkuli.utils.*
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.statement.Query

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

enum class ReservationValidity {
    ValidUntilFurtherNotice,
    ValidUntilNextSeason,
}

data class Harbor(
    val location: Location,
    val boatSpaces: List<BoatSpaceOption>
)

data class BoatSpaceOption(
    val id: Int,
    val section: String,
    val placeNumber: Int,
    val widthCm: Int,
    val lengthCm: Int,
    val priceCents: Int,
    val locationName: String,
    val amenity: BoatSpaceAmenity,
    val formattedSizes: String = "${widthCm.cmToM()} x ${lengthCm.cmToM()} m".replace('.', ',')
) {
    val priceInEuro: Double
        get() = priceCents / 100.0
}

data class BoatSpaceFilter(
    val boatType: BoatType? = null,
    val boatWidth: Int? = null,
    val boatLength: Int? = null,
    val amenities: List<BoatSpaceAmenity>? = null,
    val boatSpaceType: BoatSpaceType? = null,
    val locationIds: List<Int>? = null
)

fun amenityFilter(
    amenity: BoatSpaceAmenity,
    boatWidth: Int?,
    boatLength: Int?
): SqlExpr {
    val placeDimensions = BoatSpaceConfig.getRequiredDimensions(amenity, Dimensions(boatWidth ?: 0, boatLength ?: 0))
    return AndExpr(
        listOf(
            OperatorExpr("amenity", "=", amenity),
            OperatorExpr("width_cm", ">=", placeDimensions.width),
            OperatorExpr("length_cm", ">=", placeDimensions.length)
        )
    )
}

fun createAmenityFilter(filter: BoatSpaceFilter): SqlExpr {
    if (filter.boatLength != null && filter.boatLength > BoatSpaceConfig.BOAT_LENGTH_THRESHOLD_CM) {
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
            if (it == BoatSpaceAmenity.None) {
                OperatorExpr("amenity", "=", BoatSpaceAmenity.None)
            } else {
                amenityFilter(it, filter.boatWidth, filter.boatLength)
            }
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

fun Handle.getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): Pair<List<Harbor>, Int> {
    val amenityFilter = createAmenityFilter(params)
    val locationIds =
        if (params.locationIds.isNullOrEmpty()) {
            createQuery(
                "SELECT id FROM location"
            ).mapTo<Int>().toList()
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
            boat_space.id,
            section, 
            place_number,
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
            (boat_space_reservation.status = 'Info' AND boat_space_reservation.created > NOW() - INTERVAL '30 minutes') OR
            (boat_space_reservation.status = 'Payment' AND boat_space_reservation.created > NOW() - INTERVAL '24 hours') OR
            (boat_space_reservation.status = 'Confirmed') 
        )
        WHERE 
            boat_space_reservation.id IS NULL
            AND ${combinedFilter.toSql()}
            
        ORDER BY price, length_cm, width_cm
        """.trimIndent()

    val query = createQuery(sql)

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
                            address = ""
                        ),
                    boatSpaces = spaces
                )
            }

    return Pair(harbors, count)
}
