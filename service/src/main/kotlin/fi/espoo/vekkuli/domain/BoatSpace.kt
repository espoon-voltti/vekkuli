package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.utils.*
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

fun Handle.getUnreservedBoatSpaceOptions(params: BoatSpaceFilter): List<Harbor> {
    val filter =
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
            ${if (params.boatSpaceType !== null) "AND type = :boatSpaceType" else ""}
            ${if (!params.locationIds.isNullOrEmpty()) "AND location.id IN (<locationIds>)" else ""}
            AND ${filter.toSql()}
            
        ORDER BY price 
        """.trimIndent()

    val query = createQuery(sql)
    if (params.boatSpaceType != null) {
        query.bind("boatSpaceType", params.boatSpaceType)
    }
    if (!params.locationIds.isNullOrEmpty()) {
        query.bindList("locationIds", params.locationIds)
    }
    filter.bind(query)

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
