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

data class Harbor(val location: Location, val boatSpaces: List<BoatSpaceOption>)

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

fun Handle.getUnreservedBoatSpaceOptions(
    width: Int? = null,
    length: Int? = null,
    amenities: List<BoatSpaceAmenity>? = null,
    boatSpaceType: BoatSpaceType? = null,
    locationIds: List<Int>? = null
): List<Harbor> {
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
            ${if (width != null) "AND width_cm >= :minWidth AND width_cm <= :maxWidth" else ""}
            ${if (length != null) "AND length_cm >= :minLength AND length_cm <= :maxLength" else ""}
            ${if (!amenities.isNullOrEmpty()) "AND amenity IN (<amenities>)" else ""}
            ${if (boatSpaceType != null) "AND type = :boatSpaceType" else ""}
            ${if (!locationIds.isNullOrEmpty()) "AND location.id IN (<locationIds>)" else ""}
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
