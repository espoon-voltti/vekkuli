package fi.espoo.vekkuli.boatSpace.employeeReservationList

import fi.espoo.vekkuli.domain.Boat
import fi.espoo.vekkuli.domain.BoatSpaceAmenity
import fi.espoo.vekkuli.domain.BoatSpaceReservationItem
import fi.espoo.vekkuli.domain.BoatSpaceType
import fi.espoo.vekkuli.domain.BoatType
import fi.espoo.vekkuli.domain.OwnershipStatus
import fi.espoo.vekkuli.domain.ReservationStatus
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.domain.StorageType
import fi.espoo.vekkuli.repository.PaginatedResult
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.utils.PaginationExpr
import fi.espoo.vekkuli.utils.SqlExpr
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import kotlin.collections.component1
import kotlin.collections.component2

data class BoatSpaceReservationItemWithWarningRow(
    val id: Int,
    val boatSpaceId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val reserverId: UUID,
    val actingCitizenId: UUID?,
    val reserverType: ReserverType,
    val name: String,
    val email: String,
    val phone: String,
    val type: BoatSpaceType,
    val place: String,
    val locationName: String,
    val storageType: StorageType?,
    // Boat
    val boatId: Int?,
    val boatRegistrationCode: String?,
    val amenity: BoatSpaceAmenity,
    val boatReserverId: UUID?,
    val boatName: String?,
    val boatWidthCm: Int?,
    val boatLengthCm: Int?,
    val boatDepthCm: Int?,
    val boatWeightKg: Int?,
    val boatType: BoatType?,
    val boatOtherIdentification: String?,
    val boatExtraInformation: String?,
    val boatOwnership: OwnershipStatus?,
    val boatDeletedAt: LocalDateTime?,
    // Trailer
    val trailerId: Int?,
    val trailerReserverId: UUID?,
    val trailerRegistrationCode: String?,
    val trailerWidthCm: Int?,
    val trailerLengthCm: Int?,
    val warning: String?,
    val section: String,
    val municipalityCode: Int,
    val municipalityName: String,
    val paymentDate: LocalDate?,
    val invoiceDueDate: LocalDate?,
    val validity: ReservationValidity
)

fun getFilteredBoatSpaceReservationWarningCount(
    jdbi: Jdbi,
    filter: SqlExpr
): Int =
    jdbi.withHandleUnchecked { handle ->
        val sqlParts = mutableListOf<String>()
        sqlParts.add(
            """
            SELECT COUNT(distinct(bsr.id)) as count
            FROM boat_space_reservation bsr
            JOIN reserver r ON bsr.reserver_id = r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN reservation_warning rw ON bsr.id = rw.reservation_id
            """.trimIndent()
        )
        sqlParts.add("WHERE")
        sqlParts.add(filter.toSql())

        val query =
            handle.createQuery(sqlParts.joinToString(" ")).apply {
                filter.bind(this)
            }

        query.mapTo<Int>().findOne().orElse(0)
    }

fun getFilteredAndPaginatedBoatSpaceReservationIds(
    jdbi: Jdbi,
    filter: SqlExpr,
    sortBy: BoatSpaceReservationSortBy?,
    pagination: PaginationExpr?
): PaginatedResult<Int> =
    jdbi.withHandleUnchecked { handle ->
        val sqlParts = mutableListOf<String>()
        sqlParts.add(
            """
            SELECT
                COUNT(*) OVER() as total_count,
                bsr.id,
                CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                location.name as location_name, 
                m.name as municipality_name,
                r.name,
                bs.type
            FROM boat_space_reservation bsr
            JOIN reserver r ON bsr.reserver_id = r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN municipality m ON r.municipality_code = m.code
            LEFT JOIN LATERAL (
                SELECT rw.key, rw.created 
                FROM reservation_warning rw 
                WHERE rw.reservation_id = bsr.id
                ORDER BY rw.created DESC
                LIMIT 1
            ) rw ON TRUE
            """.trimIndent()
        )
        sqlParts.add("WHERE")
        sqlParts.add(filter.toSql())
        sortBy?.apply()?.takeIf { it.isNotEmpty() }?.let { sqlParts.add(it) }
        pagination?.toSql()?.let { sqlParts.add(it) }

        val query =
            handle.createQuery(sqlParts.joinToString(" ")).apply {
                filter.bind(this)
                pagination?.bind(this)
            }

        val results =
            query
                .map { rs, _ ->
                    rs.getInt("total_count") to rs.getInt("id")
                }.list()

        val totalRows = results.firstOrNull()?.first ?: 0
        val ids = results.map { it.second }

        PaginatedResult(ids, totalRows, pagination?.start ?: 0, pagination?.end ?: totalRows)
    }

fun getBoatSpaceReservationItemsByIds(
    jdbi: Jdbi,
    ids: List<Int>,
    sortBy: BoatSpaceReservationSortBy?,
): List<BoatSpaceReservationItem> =
    jdbi.withHandleUnchecked { handle ->
        val sqlParts = mutableListOf<String>()
        sqlParts.add(
            """
            SELECT
                bsr.id, bsr.reserver_id, bsr.boat_space_id, bsr.start_date, bsr.end_date, 
                bsr.status, bsr.created, bsr.updated, bsr.employee_id,
                bsr.acting_citizen_id, bsr.validity, bsr.original_reservation_id, bsr.termination_reason,
                bsr.termination_comment, bsr.termination_timestamp,
                bsr.storage_type,
                r.email, r.phone, r.type as reserver_type, r.name,
                r.municipality_code,
                location.name as location_name, 
                bs.type, bs.place_number, bs.amenity,
                CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
                rw.key as warning,
                bs.section,
                m.name as municipality_name,
                p.paid as payment_date,
                b.id AS boat_id,
                b.registration_code AS boat_registration_code,
                b.reserver_id AS boat_reserver_id,
                b.name AS boat_name,
                b.width_cm AS boat_width_cm,
                b.length_cm AS boat_length_cm,
                b.depth_cm AS boat_depth_cm,
                b.weight_kg AS boat_weight_kg,
                b.type AS boat_type,
                b.other_identification AS boat_other_identification,
                b.extra_information AS boat_extra_information,
                b.ownership AS boat_ownership,
                b.deleted_at AS boat_deleted_at,
                t.id AS trailer_id,
                t.reserver_id AS trailer_reserver_id,
                t.registration_code AS trailer_registration_code,
                t.width_cm AS trailer_width_cm,
                t.length_cm AS trailer_length_cm,
                i.due_date as invoice_due_date
            FROM boat_space_reservation bsr
            LEFT JOIN boat b on b.id = bsr.boat_id
            LEFT JOIN trailer t on t.id = bsr.trailer_id
            JOIN reserver r ON bsr.reserver_id = r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN municipality m ON r.municipality_code = m.code
            LEFT JOIN reservation_warning rw ON rw.reservation_id = bsr.id
            LEFT JOIN payment p ON (p.reservation_id = bsr.id AND p.status = 'Success')
            LEFT JOIN invoice i ON bsr.id = i.reservation_id
            """.trimIndent()
        )
        sqlParts.add("WHERE bsr.id = ANY(:ids)")
        sortBy?.apply()?.takeIf { it.isNotEmpty() }?.let { sqlParts.add(it) }

        val query = handle.createQuery(sqlParts.joinToString(" "))

        query
            .bind("ids", ids.toIntArray())
            .mapTo<BoatSpaceReservationItemWithWarningRow>()
            .list()
            .groupBy { it.id }
            .map { (id, warnings) ->
                val row = warnings.first()
                BoatSpaceReservationItem(
                    id = id,
                    boatSpaceId = row.boatSpaceId,
                    startDate = row.startDate,
                    endDate = row.endDate,
                    status = row.status,
                    reserverId = row.reserverId,
                    name = row.name,
                    email = row.email,
                    phone = row.phone,
                    type = row.type,
                    place = row.place,
                    section = row.section,
                    locationName = row.locationName,
                    boat =
                        if (row.boatId == null) {
                            null
                        } else {
                            Boat(
                                id = row.boatId,
                                registrationCode = row.boatRegistrationCode,
                                reserverId = row.boatReserverId ?: throw IllegalStateException("Boat reserver id is null"),
                                name = row.boatName,
                                widthCm = row.boatWidthCm ?: throw IllegalStateException("Boat width is null"),
                                lengthCm = row.boatLengthCm ?: throw IllegalStateException("Boat length is null"),
                                depthCm = row.boatDepthCm ?: throw IllegalStateException("Boat depth is null"),
                                weightKg = row.boatWeightKg ?: throw IllegalStateException("Boat weight is null"),
                                type = row.boatType ?: throw IllegalStateException("Boat type is null"),
                                otherIdentification = row.boatOtherIdentification,
                                extraInformation = row.boatExtraInformation,
                                ownership = row.boatOwnership ?: throw IllegalStateException("Boat ownership is null"),
                                deletedAt = row.boatDeletedAt,
                                warnings = emptySet(),
                            )
                        },
                    trailer = null,
                    warnings = (warnings.mapNotNull { it.warning }).toSet(),
                    actingCitizenId = row.actingCitizenId,
                    reserverType = row.reserverType,
                    municipalityCode = row.municipalityCode,
                    municipalityName = row.municipalityName,
                    paymentDate = row.paymentDate,
                    storageType = row.storageType,
                    validity = row.validity,
                    amenity = row.amenity,
                    invoiceDueDate = row.invoiceDueDate
                )
            }
    }
