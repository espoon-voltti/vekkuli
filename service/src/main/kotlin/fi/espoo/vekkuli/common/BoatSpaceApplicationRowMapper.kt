package fi.espoo.vekkuli.common

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fi.espoo.vekkuli.domain.*
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class BoatSpaceApplicationRowMapper : RowMapper<BoatSpaceApplication> {
    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }

    override  fun map(rs: ResultSet, ctx: StatementContext): BoatSpaceApplication {
        val locationWishesJson = rs.getString("location_wishes")
        val locationWishes: List<LocationWish> = objectMapper.readValue(locationWishesJson, object : TypeReference<List<LocationWish>>() {})

        return BoatSpaceApplication(
            createdAt = rs.getString("created_at"),
            type = BoatSpaceType.valueOf(rs.getString("type")), // Assuming enum
            boatType = BoatType.valueOf(rs.getString("boat_type")), // Assuming enum
            amenity = BoatSpaceAmenity.valueOf(rs.getString("amenity")), // Assuming enum
            boatWidthCm = rs.getInt("boat_width_cm"),
            boatLengthCm = rs.getInt("boat_length_cm"),
            boatWeightKg = rs.getInt("boat_weight_kg"),
            boatRegistrationCode = rs.getString("boat_registration_code"),
            information = rs.getString("information"),
            totalCount = rs.getInt("total_count"),
            locationWishes = locationWishes
        )
    }
}