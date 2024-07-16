package fi.espoo.vekkuli.domain

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
    val price: Int,
    val locationName: String,
    val amenity: BoatSpaceAmenity,
    val formattedSizes: String = "${widthCm / 100.0} x ${lengthCm / 100.0} m".replace('.', ',')
)

data class BoatSpaceFilter(
    val boatType: BoatType? = null,
    val boatWidth: Int? = null,
    val boatLength: Int? = null,
    val amenities: List<BoatSpaceAmenity>? = null,
    val boatSpaceType: BoatSpaceType? = null,
    val locationIds: List<Int>? = null
)

fun beamFilter(
    boatWidth: Int?,
    boatLength: Int?
): SqlExpr =
    AndExpr(
        listOf(
            OperatorExpr("amenity", "=", BoatSpaceAmenity.Beam),
            OperatorExpr("width_cm", ">=", boatWidth?.plus(40)),
            OperatorExpr("length_cm", ">=", boatLength?.minus(100))
        )
    )

fun walkBeamFilter(
    boatWidth: Int?,
    boatLength: Int?
): SqlExpr =
    AndExpr(
        listOf(
            OperatorExpr("amenity", "=", BoatSpaceAmenity.WalkBeam),
            OperatorExpr("width_cm", ">=", boatWidth?.plus(75)),
            OperatorExpr("length_cm", ">=", boatLength?.minus(100))
        )
    )

fun rearBuoyFilter(
    boatWidth: Int?,
    boatLength: Int?
): SqlExpr =
    AndExpr(
        listOf(
            OperatorExpr("amenity", "=", BoatSpaceAmenity.RearBuoy),
            OperatorExpr("width_cm", ">=", boatWidth?.plus(50)),
            OperatorExpr("length_cm", ">=", boatLength?.plus(300))
        )
    )

fun createAmenityFilter(filter: BoatSpaceFilter): SqlExpr {
    val amenitites = if (filter.amenities == null || filter.amenities.isEmpty()) BoatSpaceAmenity.entries.toList() else filter.amenities
    return OrExpr(
        amenitites.map {
            when (it) {
                BoatSpaceAmenity.None -> OperatorExpr("amenity", "=", BoatSpaceAmenity.None)
                BoatSpaceAmenity.Beam -> beamFilter(filter.boatWidth, filter.boatLength)
                BoatSpaceAmenity.WalkBeam -> walkBeamFilter(filter.boatWidth, filter.boatLength)
                BoatSpaceAmenity.RearBuoy -> rearBuoyFilter(filter.boatWidth, filter.boatLength)
                BoatSpaceAmenity.Buoy -> OperatorExpr("amenity", "=", BoatSpaceAmenity.Buoy)
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
    private val locationIds: List<Int>?,
    private val boatType: BoatType?
) : SqlExpr() {
    private val boatTypeVar: String? = if (boatType != null) "bs_${getNextIndex()}" else null
    private val expr =
        if (!locationIds.isNullOrEmpty()) {
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

fun Handle.getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): List<Harbor> {
    val amenityFilter =
        if (params.boatLength != null && params.boatLength > 1500) {
            // Boats over 15 meters will only fit in buoys
            OperatorExpr(
                "amenity",
                "=",
                BoatSpaceAmenity.Buoy,
            )
        } else {
            createAmenityFilter(params)
        }
    val locationFilter = LocationFilter(params.locationIds, params.boatType)
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
            price.price as price,
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
            
        ORDER BY price 
        """.trimIndent()

    val query = createQuery(sql)

    combinedFilter.bind(query)

    val boatSpaces = query.mapTo<BoatSpaceOption>().toList()

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

    return harbors
}
