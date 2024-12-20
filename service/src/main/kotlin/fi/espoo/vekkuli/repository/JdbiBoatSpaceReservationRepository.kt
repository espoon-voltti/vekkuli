package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.boatSpace.terminateReservation.ReservationTerminationReason
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.utils.SqlExpr
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.core.statement.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class BoatSpaceReservationDetailsRow(
    val id: Int,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
    val boatSpaceId: Int,
    val boatSpaceLengthCm: Int,
    val boatSpaceWidthCm: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: ReservationStatus,
    val terminationReason: ReservationTerminationReason?,
    val terminationComment: String?,
    val terminationTimestamp: LocalDateTime?,
    val reserverType: ReserverType,
    val reserverId: UUID,
    val actingCitizenId: UUID?,
    val name: String,
    val email: String,
    val phone: String,
    val streetAddress: String?,
    val postalCode: String?,
    val municipalityCode: Int,
    val municipalityName: String,
    val type: BoatSpaceType,
    val place: String,
    val locationId: Int,
    val locationName: String,
    val registrationCode: String?,
    val amenity: BoatSpaceAmenity,
    val validity: ReservationValidity,
    val originalReservationId: Int? = null,
    val paymentDate: LocalDate?,
    val paymentId: UUID?,
    val storageType: StorageType?,
    // Boat
    val boatId: Int?,
    val boatRegistrationCode: String?,
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
)

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
    val validity: ReservationValidity
)

@Repository
class JdbiBoatSpaceReservationRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) : BoatSpaceReservationRepository {
    override fun getBoatSpaceReservationIdForPayment(paymentId: UUID): Int =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.id
                    FROM boat_space_reservation bsr
                    JOIN payment AS p ON bsr.id = p.reservation_id
                    WHERE p.id = :paymentId
                        AND bsr.status = 'Payment' 
                        AND bsr.created > :currentTime - make_interval(secs => :paymentTimeout)
                    """.trimIndent()
                )
            query.bind("paymentId", paymentId)
            query.bind("paymentTimeout", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<Int>().findOne().orElse(null)
        }

    fun getExcludedBoatTypes(
        handle: Handle,
        locationId: Int
    ): List<BoatType> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT excluded_boat_type
                    FROM harbor_restriction
                    WHERE location_id = :locationId
                    """.trimIndent()
                )
            query.bind("locationId", locationId)
            query.mapTo<BoatType>().list()
        }

    fun getWarningsForReservation(
        reservationId: Int,
        boatId: Int?,
        trailerId: Int?
    ): List<ReservationWarning> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle
                    .createQuery(
                        """
                        SELECT *
                        FROM reservation_warning
                        WHERE reservation_id = :reservationId
                          ${if (boatId != null) "AND boat_id = :boatId" else ""}
                          ${if (trailerId != null) "AND trailer_id = :trailerId" else ""}
                        """.trimIndent()
                    ).bind("reservationId", reservationId)

            if (boatId != null) {
                query.bind("boatId", boatId)
            }
            if (trailerId != null) {
                query.bind("trailerId", trailerId)
            }

            query
                .mapTo<ReservationWarning>()
                .list()
                .groupBy { "${it.reservationId}${it.key}${it.boatId}${it.trailerId}" }
                .map { it.value.first() }
        }

    fun loadBoatForReserver(
        handle: Handle,
        reservationId: Int,
        boatId: Int?
    ): Boat? {
        if (boatId == null) {
            return null
        }
        val query =
            handle.createQuery(
                """
                SELECT * FROM boat
                WHERE id = :boatId
                """.trimIndent()
            )
        query.bind("boatId", boatId)
        val boat = query.mapTo<Boat>().findOne().orElse(null)
        if (boat == null) {
            return null
        }
        val warnings = getWarningsForReservation(reservationId, boatId, null).map { it.key }.toSet()
        return boat.copy(warnings = warnings)
    }

    fun loadTrailerForReserver(
        handle: Handle,
        reservationId: Int,
        trailerId: Int?
    ): Trailer? {
        if (trailerId == null) {
            return null
        }
        val query =
            handle.createQuery(
                """
                SELECT * FROM trailer
                WHERE id = :trailerId
                """.trimIndent()
            )
        query.bind("trailerId", trailerId)
        val trailer = query.mapTo<Trailer>().findOne().orElse(null)
        if (trailer == null) {
            return null
        }
        val warnings = getWarningsForReservation(reservationId, null, trailerId).map { it.key }.toSet()
        return trailer.copy(warnings = warnings)
    }

    override fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectPartForBoatSpaceReservationDetails()}
                    FROM payment p
                    JOIN boat_space_reservation bsr ON p.reservation_id = bsr.id
                    LEFT JOIN boat b ON b.id = bsr.boat_id
                    LEFT JOIN trailer t ON t.id = bsr.trailer_id
                    JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location.id = bs.location_id
                    JOIN price ON bs.price_id = price.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE p.id = :paymentId              
                    """.trimIndent()
                )
            query.bind("paymentId", id)
            buildBoatSpaceReservationDetails(query, handle)
        }

    private fun buildBoatSpaceReservationDetails(
        query: Query,
        handle: Handle,
    ): BoatSpaceReservationDetails? {
        val dbResult = query.mapTo<BoatSpaceReservationDetailsRow>().findOne().orElse(null)
        return if (dbResult != null) {
            BoatSpaceReservationDetails(
                id = dbResult.id,
                created = dbResult.created,
                updated = dbResult.updated,
                priceCents = dbResult.priceCents,
                vatCents = dbResult.vatCents,
                netPriceCents = dbResult.netPriceCents,
                boatSpaceId = dbResult.boatSpaceId,
                startDate = dbResult.startDate,
                endDate = dbResult.endDate,
                status = dbResult.status,
                terminationReason = dbResult.terminationReason,
                terminationComment = dbResult.terminationComment,
                terminationTimestamp = dbResult.terminationTimestamp,
                reserverType = dbResult.reserverType,
                reserverId = dbResult.reserverId,
                actingCitizenId = dbResult.actingCitizenId,
                name = dbResult.name,
                email = dbResult.email,
                phone = dbResult.phone,
                streetAddress = dbResult.streetAddress,
                postalCode = dbResult.postalCode,
                municipalityCode = dbResult.municipalityCode,
                municipalityName = dbResult.municipalityName,
                type = dbResult.type,
                place = dbResult.place,
                locationName = dbResult.locationName,
                boatSpaceLengthCm = dbResult.boatSpaceLengthCm,
                boatSpaceWidthCm = dbResult.boatSpaceWidthCm,
                amenity = dbResult.amenity,
                validity = dbResult.validity,
                originalReservationId = dbResult.originalReservationId,
                paymentDate = dbResult.paymentDate,
                paymentId = dbResult.paymentId,
                excludedBoatTypes = getExcludedBoatTypes(handle, dbResult.locationId),
                boat = loadBoatForReserver(handle, dbResult.id, dbResult.boatId),
                trailer = loadTrailerForReserver(handle, dbResult.id, dbResult.trailerId),
                storageType = dbResult.storageType,
            )
        } else {
            null
        }
    }

    override fun updateBoatSpaceReservationOnPaymentSuccess(paymentId: UUID): Int? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation bsr
                    SET status = 'Confirmed', updated = :updatedTime
                    FROM payment AS p
                    WHERE p.id = :paymentId
                        AND bsr.id = p.reservation_id
                        AND bsr.status = 'Payment' 
                        AND bsr.created > :currentTime - make_interval(secs => :paymentTimeout)
                    RETURNING bsr.id
                    """.trimIndent()
                )
            query.bind("paymentId", paymentId)
            query.bind("paymentTimeout", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("updatedTime", timeProvider.getCurrentDateTime())
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<Int>().findOne().orElse(null)
        }

    override fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinForReservationWithDependencies()}
                    WHERE bsr.acting_citizen_id = :id
                        AND bsr.status IN ('Info', 'Payment') 
                        AND :currentTime BETWEEN bsr.created AND bsr.created + make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())

            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinForReservationWithDependencies()}
                    WHERE bsr.employee_id = :id
                        AND bsr.status = 'Info' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationsForTrailer(trailerId: Int): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinPartForBoatSpaceReservationDetails()}
                    WHERE trailer_id = :trailerId
                    """.trimIndent()
                )
            query.bind("trailerId", trailerId)

            // read warnings that are associated with the reservation
            val reservations = query.mapTo<BoatSpaceReservationDetailsRow>().list()
            reservations.map {
                val warningQuery =
                    handle.createQuery(
                        """
                        SELECT key
                        FROM reservation_warning
                        WHERE reservation_id = :reservationId
                        """.trimIndent()
                    )
                warningQuery.bind("reservationId", it.id)
                BoatSpaceReservationDetails(
                    id = it.id,
                    created = it.created,
                    updated = it.updated,
                    priceCents = it.priceCents,
                    vatCents = it.vatCents,
                    netPriceCents = it.netPriceCents,
                    boatSpaceId = it.boatSpaceId,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    status = it.status,
                    terminationReason = it.terminationReason,
                    terminationComment = it.terminationComment,
                    terminationTimestamp = it.terminationTimestamp,
                    reserverType = it.reserverType,
                    reserverId = it.reserverId,
                    actingCitizenId = it.actingCitizenId,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    streetAddress = it.streetAddress,
                    postalCode = it.postalCode,
                    municipalityCode = it.municipalityCode,
                    municipalityName = it.municipalityName,
                    type = it.type,
                    place = it.place,
                    locationName = it.locationName,
                    boat = loadBoatForReserver(handle, it.id, it.boatId),
                    trailer = loadTrailerForReserver(handle, it.id, it.trailerId),
                    boatSpaceLengthCm = it.boatSpaceLengthCm,
                    boatSpaceWidthCm = it.boatSpaceWidthCm,
                    amenity = it.amenity,
                    validity = it.validity,
                    excludedBoatTypes = emptyList(),
                    originalReservationId = it.originalReservationId,
                    paymentDate = it.paymentDate,
                    paymentId = it.paymentId,
                    storageType = it.storageType
                )
            }
        }

    override fun getReservationWithReserverInInfoPaymentRenewalStateWithinSessionTime(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinForReservationWithDependencies()}
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment')
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationReserverEmail(reservationId: Int): Recipient? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT r.email, r.id
                    FROM boat_space_reservation bsr
                    LEFT JOIN reserver r ON bsr.reserver_id = r.id
                    WHERE bsr.id = :reservationId
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.mapTo<Recipient>().findOne().orElse(null)
        }

    override fun getReservationWithDependencies(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinForReservationWithDependencies()}
                    WHERE bsr.id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationForRenewal(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinForReservationWithDependencies()}
                    WHERE bsr.id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    // note - this is widely different from other "ReservationWithDependencies" return types
    override fun getReservationWithoutReserver(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents,
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description,
                        ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types,
                        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment')
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    GROUP BY bsr.id, location.name, price.id, bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun removeBoatSpaceReservation(id: Int): Unit =
        jdbi.inTransactionUnchecked { tx ->
            tx
                .createUpdate(
                    """
                    DELETE FROM payment
                    WHERE reservation_id = :id
                    """.trimIndent()
                ).bind("id", id)
                .execute()
            tx
                .createUpdate(
                    """
                    DELETE FROM boat_space_reservation
                    WHERE id = :id
                    """.trimIndent()
                ).bind("id", id)
                .execute()
        }

    override fun getBoatSpaceReservationsForReserver(
        reserverId: UUID,
        spaceType: BoatSpaceType?
    ): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinPartForBoatSpaceReservationDetails()}
                    WHERE r.id = :reserverId AND 
                      ${if (spaceType != null) "bs.type = :spaceType AND" else ""}
                        (
                            ((bsr.status = 'Confirmed' OR bsr.status = 'Invoiced') AND bsr.end_date >= :endDateCut) OR
                             (bsr.status = 'Cancelled' AND bsr.end_date > :endDateCut) 
                        )
                    """.trimIndent()
                )
            if (spaceType != null) {
                query.bind("spaceType", spaceType)
            }
            query.bind("reserverId", reserverId)
            query.bind("endDateCut", timeProvider.getCurrentDate())

            // read warnings that are associated with the reservation
            val reservations = query.mapTo<BoatSpaceReservationDetailsRow>().list()
            reservations.map {
                BoatSpaceReservationDetails(
                    id = it.id,
                    created = it.created,
                    updated = it.updated,
                    priceCents = it.priceCents,
                    vatCents = it.vatCents,
                    netPriceCents = it.netPriceCents,
                    boatSpaceId = it.boatSpaceId,
                    startDate = it.startDate,
                    endDate = it.endDate,
                    status = it.status,
                    terminationReason = it.terminationReason,
                    terminationComment = it.terminationComment,
                    terminationTimestamp = it.terminationTimestamp,
                    reserverType = it.reserverType,
                    reserverId = it.reserverId,
                    actingCitizenId = it.actingCitizenId,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    streetAddress = it.streetAddress,
                    postalCode = it.postalCode,
                    municipalityCode = it.municipalityCode,
                    municipalityName = it.municipalityName,
                    type = it.type,
                    place = it.place,
                    locationName = it.locationName,
                    boat = loadBoatForReserver(handle, it.id, it.boatId),
                    trailer = loadTrailerForReserver(handle, it.id, it.trailerId),
                    boatSpaceLengthCm = it.boatSpaceLengthCm,
                    boatSpaceWidthCm = it.boatSpaceWidthCm,
                    amenity = it.amenity,
                    validity = it.validity,
                    excludedBoatTypes = emptyList(),
                    originalReservationId = it.originalReservationId,
                    paymentDate = it.paymentDate,
                    paymentId = it.paymentId,
                    storageType = it.storageType
                )
            }
        }

    override fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinPartForBoatSpaceReservationDetails()}
                    WHERE bsr.id = :reservationId
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            buildBoatSpaceReservationDetails(query, handle)
        }

    override fun getBoatSpaceRelatedToReservation(reservationId: Int): BoatSpace? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bs.*, location.name as location_name, bsr,
                        ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location.id = bs.location_id
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE bsr.id = :reservationId
                    GROUP BY bs.id, location.name, bsr 
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.mapTo<BoatSpace>().findOne().orElse(null)
        }

    override fun getBoatSpaceReservations(
        filter: SqlExpr,
        sortBy: BoatSpaceReservationSortBy?
    ): List<BoatSpaceReservationItem> =
        jdbi.withHandleUnchecked { handle ->
            val filterQuery = filter.toSql()
            val sortByQuery = sortBy?.apply()?.takeIf { it.isNotEmpty() } ?: ""
            val query =
                handle.createQuery(
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
                        bs.type, bs.place_number, 
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
                        t.length_cm AS trailer_length_cm
                    FROM boat_space_reservation bsr
                    LEFT JOIN boat b on b.id = bsr.boat_id
                    LEFT JOIN trailer t on t.id = bsr.trailer_id
                    JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN municipality m ON r.municipality_code = m.code
                    LEFT JOIN reservation_warning rw ON rw.reservation_id = bsr.id
                    LEFT JOIN payment p ON (p.reservation_id = bsr.id AND p.status = 'Success')
                    WHERE $filterQuery
                    $sortByQuery
                    """.trimIndent()
                )
            filter.bind(query)
            query
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
                        validity = row.validity
                    )
                }
        }

    override fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingCitizenId: UUID?,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO boat_space_reservation (reserver_id, acting_citizen_id, boat_space_id, start_date, end_date, created, creation_type)
                    VALUES (:reserverId, :actingCitizenId, :boatSpaceId, :startDate, :endDate, :currentDate, :creationType)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("actingCitizenId", actingCitizenId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.bind("currentDate", timeProvider.getCurrentDateTime())
            query.bind("creationType", creationType)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
        creationType: CreationType,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO boat_space_reservation (employee_id, boat_space_id, start_date, end_date, created, validity, creation_type)
                    VALUES (:employeeId, :boatSpaceId, :startDate, :endDate, :currentDate, :validity, :creationType)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("employeeId", employeeId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.bind("currentDate", timeProvider.getCurrentDateTime())
            query.bind("validity", ReservationValidity.Indefinite)
            query.bind("creationType", creationType)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateBoatInBoatSpaceReservation(
        reservationId: Int,
        boatId: Int,
        reserverId: UUID,
        reservationStatus: ReservationStatus,
        validity: ReservationValidity,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = :status, updated = :updatedTime, boat_id = :boatId, reserver_id = :reserverId, validity = :validity, start_date = :startDate, end_date = :endDate
                    WHERE id = :id
                        AND (status = 'Info' OR status = 'Payment')
                        AND created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("status", reservationStatus)
            query.bind("updatedTime", timeProvider.getCurrentDateTime())
            query.bind("id", reservationId)
            query.bind("boatId", boatId)
            query.bind("reserverId", reserverId)
            query.bind("validity", validity)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateTrailerInBoatSpaceReservation(
        reservationId: Int,
        trailerId: Int
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET trailer_id = :trailerId
                    WHERE id = :reservationId
                        AND (status = 'Info' OR status = 'Payment')
                        AND created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.bind("trailerId", trailerId)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateStorageType(
        reservationId: Int,
        storageType: StorageType
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET storage_type = :storageType
                    WHERE id = :reservationId
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.bind("storageType", storageType)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun setReservationStatusToInvoiced(reservationId: Int): BoatSpaceReservation =
        setReservationStatus(reservationId, ReservationStatus.Invoiced)

    private fun setReservationStatus(
        reservationId: Int,
        status: ReservationStatus,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = :reservationStatus, updated = :updatedTime
                    WHERE id = :reservationId
                        AND (status = 'Payment' OR status = 'Info')
                        AND created > :currentTime - make_interval(secs => :paymentTimeout)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)

            query.bind("reservationStatus", status)
            query.bind("updatedTime", timeProvider.getCurrentDateTime())
            query.bind("paymentTimeout", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = 'Confirmed', updated = :updatedTime
                    WHERE id = :id
                        AND (status = 'Invoiced' OR status = 'Payment')
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", reservationId)
            query.bind("updatedTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservation>().singleOrNull()
        }

    override fun getExpiredBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                     ${buildSqlSelectFromJoinPartForBoatSpaceReservationDetails()}
                    WHERE r.id = :reserverId AND (
                        (
                            bsr.status = 'Cancelled' AND 
                            bsr.end_date <= :endDateCut
                        )
                        OR 
                        (
                            (bsr.status = 'Confirmed' OR bsr.status = 'Invoiced') AND
                            bsr.end_date < :endDateCut
                         )
                     )
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("endDateCut", timeProvider.getCurrentDate())

            query.mapTo<BoatSpaceReservationDetails>().list()
        }

    override fun getExpiringBoatSpaceReservations(validity: ReservationValidity): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectFromJoinPartForBoatSpaceReservationDetails()}
                    WHERE bsr.status = 'Confirmed' AND validity = :validity
                        AND end_date < :endDateCut AND end_date > :currentTime
                    """.trimIndent()
                )
            query.bind("validity", validity)
            query.bind(
                "endDateCut",
                timeProvider.getCurrentDate().plusDays(BoatSpaceConfig.DAYS_BEFORE_RESERVATION_EXPIRY_NOTICE.toLong())
            )
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservationDetails>().list()
        }

    override fun setReservationAsExpired(reservationId: Int): Unit =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
                    UPDATE boat_space_reservation
                    SET updated = :updatedTime, end_date = :endDate
                    WHERE id = :id
                    """.trimIndent()
                ).bind("id", reservationId)
                .bind("endDate", timeProvider.getCurrentDate().minusDays(1))
                .bind("updatedTime", timeProvider.getCurrentDateTime())
                .execute()
        }

    override fun getHarbors(): List<Location> =
        jdbi.withHandleUnchecked { handle ->
            handle.createQuery("SELECT * FROM location").mapTo<Location>().toList()
        }

    private fun buildSqlSelectPartForBoatSpaceReservationDetails() =
        """
        SELECT bsr.id,
        bsr.start_date,
        bsr.end_date,
        bsr.created,
        bsr.updated,
        bsr.status,
        bsr.boat_space_id,
        bsr.validity,
        bsr.original_reservation_id,
        bsr.termination_reason,
        bsr.termination_comment,
        bsr.termination_timestamp,
        bsr.storage_type,
        bsr.acting_citizen_id,
        p.id as payment_id,
        p.paid as payment_date,
        r.id as reserver_id,
        r.type as reserver_type,
        r.name,
        r.email, 
        r.phone,
        r.street_address,
        r.postal_code,
        r.municipality_code,
        m.name as municipality_name,
        location.name as location_name, 
        location.id as location_id,
        bs.type,
        bs.length_cm as boat_space_length_cm,
        bs.width_cm as boat_space_width_cm,
        bs.amenity,
        price.price_cents,
        price.vat_cents,
        price.net_price_cents,
        CONCAT(bs.section, ' ', TO_CHAR(bs.place_number, 'FM000')) as place,
        
        b.id as boat_id,
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
        t.length_cm AS trailer_length_cm
        """.trimIndent()

    private fun buildSqlSelectFromJoinPartForBoatSpaceReservationDetails() =
        """
        ${buildSqlSelectPartForBoatSpaceReservationDetails()}
        FROM boat_space_reservation bsr
        LEFT JOIN boat b ON b.id = bsr.boat_id
        LEFT JOIN trailer t ON t.id = bsr.trailer_id
        JOIN reserver r ON bsr.reserver_id =  r.id
        JOIN boat_space bs ON bsr.boat_space_id = bs.id
        JOIN location ON location.id = bs.location_id
        LEFT JOIN price ON price_id = price.id
        JOIN municipality m ON r.municipality_code = m.code
        LEFT JOIN payment p ON p.reservation_id = bsr.id AND p.status <> 'Failed'
        """.trimIndent()

    private fun buildSqlSelectPartForReservationWithDependencies() =
        """
        SELECT bsr.*, r.name, r.type as reserver_type, r.email, r.phone, 
          location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
          bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
          bs.description,
          CONCAT(section, ' ', TO_CHAR(place_number, 'FM000')) as place
        """.trimIndent()

    private fun buildSqlSelectFromJoinForReservationWithDependencies() =
        """
        ${buildSqlSelectPartForReservationWithDependencies()}
        FROM boat_space_reservation bsr
        LEFT JOIN reserver r ON bsr.reserver_id = r.id
        JOIN boat_space bs ON bsr.boat_space_id = bs.id
        JOIN location ON location_id = location.id
        JOIN price ON price_id = price.id
        """.trimIndent()
}
