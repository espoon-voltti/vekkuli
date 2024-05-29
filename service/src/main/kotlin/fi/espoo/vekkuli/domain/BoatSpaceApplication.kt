package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.common.BoatSpaceApplicationRowMapper
import fi.espoo.vekkuli.common.toPostgresTimestamp
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.time.LocalDateTime

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
    val name: String,
)

data class AddLocationWish(
    val locationId: Int,
    val priority: Int,
)

data class AddBoatSpaceApplication(
    val type: BoatSpaceType,
    val boatType: BoatType,
    val amenity: BoatSpaceAmenity,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatRegistrationCode: String,
    val information: String,
    val citizenId: Int,
    val locationWishes: List<AddLocationWish>,
)

data class BoatSpaceApplicationWithId(
    val id: Int,
    val createdAt: LocalDateTime,
    val type: BoatSpaceType,
    val boatType: BoatType,
    val amenity: BoatSpaceAmenity,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatRegistrationCode: String,
    val information: String,
    val citizenId: Int,
    val locationWishes: List<LocationWish>,
)

data class BoatSpaceApplicationWithTotalCount(
    val id: Int,
    val createdAt: LocalDateTime,
    val type: BoatSpaceType,
    val boatType: BoatType,
    val amenity: BoatSpaceAmenity,
    val boatWidthCm: Int,
    val boatLengthCm: Int,
    val boatWeightKg: Int,
    val boatRegistrationCode: String,
    val information: String,
    val citizenId: Int,
    val locationWishes: List<LocationWish>,
    val totalCount: Int,
)

data class BoatSpaceApplicationFilter(
    val page: Int,
    val pageSize: Int,
)

fun Handle.insertBoatSpaceApplication(app: AddBoatSpaceApplication): BoatSpaceApplicationWithId {
  val result: BoatSpaceApplicationWithId = createQuery(""" 
      INSERT INTO boat_space_application (
        created_at, 
        type, 
        boat_type, 
        amenity, 
        boat_width_cm, 
        boat_length_cm, 
        boat_weight_kg, 
        boat_registration_code, 
        citizen_id,
        information
      ) VALUES ( 
              now(), 
              :type, 
              :boatType, 
              :amenity, 
              :boatWidthCm, 
              :boatLengthCm, 
              :boatWeightKg, 
              :boatRegistrationCode, 
              :citizenId,
              :information
      )
      RETURNING *
  """.trimIndent())
      .bind("type", app.type)
      .bind("boatType", app.boatType)
      .bind("amenity", app.amenity)
      .bind("boatWidthCm", app.boatWidthCm)
      .bind("boatLengthCm", app.boatLengthCm)
      .bind("boatWeightKg", app.boatWeightKg)
      .bind("boatRegistrationCode", app.boatRegistrationCode)
      .bind("citizenId", app.citizenId)
      .bind("information", app.information)
      .map(object : RowMapper<BoatSpaceApplicationWithId> {
          override  fun map(rs: ResultSet, ctx: StatementContext): BoatSpaceApplicationWithId {
              return BoatSpaceApplicationWithId(
                  id = rs.getInt("id"),
                  createdAt = rs.getString("created_at").toPostgresTimestamp(),
                  type = app.type,
                  boatType = app.boatType,
                  amenity = app.amenity,
                  boatWidthCm = app.boatWidthCm,
                  boatLengthCm = app.boatLengthCm,
                  boatWeightKg = app.boatWeightKg,
                  boatRegistrationCode = app.boatRegistrationCode,
                  information = app.information,
                  citizenId = app.citizenId,
                  locationWishes = emptyList(),
              )
          }
      }).toList().first()

    prepareBatch("""
        INSERT INTO boat_space_application_location_wish (boat_space_application_id, location_id, priority)
        VALUES (:boatSpaceApplicationId, :locationId, :priority)
    """.trimIndent()).use { batch ->
        for (locationWish in app.locationWishes) {
            batch
                .bind("boatSpaceApplicationId", result.id)
                .bind("locationId", locationWish.locationId)
                .bind("priority", locationWish.priority)
                .add()
        }
        batch.execute()
    }
        .toList()

    return result
}

fun Handle.getBoatSpaceApplications(filter: BoatSpaceApplicationFilter): List<BoatSpaceApplicationWithTotalCount> {
    val offset = (filter.page - 1) * filter.pageSize
    val sql = StringBuilder("""
        SELECT
            bsa.*, COUNT(*) OVER() AS total_count,
            COALESCE(
                JSON_AGG(
                    JSON_BUILD_OBJECT(
                        'location_id', bsalw.location_id,
                        'priority', bsalw.priority,
                        'name', loc.name
                    )
                ) FILTER (WHERE bsalw.boat_space_application_id IS NOT NULL), '[]'
            ) AS location_wishes
        FROM
            boat_space_application bsa
        LEFT JOIN boat_space_application_location_wish bsalw
          ON bsa.id = bsalw.boat_space_application_id
        LEFT JOIN location loc
          ON bsalw.location_id = loc.id
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