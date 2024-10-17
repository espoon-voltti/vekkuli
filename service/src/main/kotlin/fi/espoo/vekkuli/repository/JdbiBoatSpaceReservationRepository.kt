package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.AndExpr
import fi.espoo.vekkuli.utils.DbUtil.Companion.buildNameSearchClause
import fi.espoo.vekkuli.utils.InExpr
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class JdbiBoatSpaceReservationRepository(
    private val jdbi: Jdbi,
) : BoatSpaceReservationRepository {
    override fun getBoatSpaceReservationIdForPayment(id: UUID): Int =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.id
                    FROM boat_space_reservation bsr
                    JOIN payment AS p ON bsr.id = p.reservation_id
                    WHERE p.id = :paymentId
                        AND bsr.status = 'Payment' 
                        AND bsr.created > NOW() - make_interval(secs => :paymentTimeout)
                    """.trimIndent()
                )
            query.bind("paymentId", id)
            query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
            query.mapTo<Int>().findOne().orElse(null)
        }

    override fun getBoatSpaceReservationWithPaymentId(id: UUID): BoatSpaceReservationDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.id,
                           bsr.start_date,
                           bsr.end_date,
                           bsr.created,
                           bsr.updated,
                           bsr.status,
                           bsr.boat_space_id,
                           r.id as reserver_id,
                           r.name,
                           r.type as reserver_type,
                           r.email, 
                           r.phone,
                           r.street_address,
                           r.postal_code,
                           r.municipality_code,
                           m.name as municipality_name,
                           b.registration_code as boat_registration_code,
                           b.ownership as boat_ownership,
                           b.id as boat_id,
                           b.name as boat_name,
                           b.width_cm as boat_width_cm,
                           b.length_cm as boat_length_cm,
                           b.weight_kg as boat_weight_kg,
                           b.depth_cm as boat_depth_cm,
                           b.type as boat_type,
                           b.other_identification as boat_other_identification,
                           b.extra_information as boat_extra_information,
                           location.name as location_name, 
                           bs.type,
                            bs.length_cm as boat_space_length_cm,
                            bs.width_cm as boat_space_width_cm,
                            bs.amenity,
                           price.price_cents,
                           CONCAT(bs.section, bs.place_number) as place,
                           ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                    FROM payment p
                    JOIN boat_space_reservation bsr ON p.reservation_id = bsr.id
                    JOIN boat b ON b.id = bsr.boat_id
                    JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location.id = bs.location_id
                    JOIN price ON bs.price_id = price.id
                    JOIN municipality m ON r.municipality_code = m.code
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE p.id = :paymentId
                    GROUP BY r.id, p.id, bsr.id, b.id, location.id, bs.id, price.id, r.email, r.phone, r.street_address, r.postal_code, r.municipality_code, r.name, r.type, m.name                
                    """.trimIndent()
                )
            query.bind("paymentId", id)
            query.mapTo<BoatSpaceReservationDetails>().findOne().orElse(null)
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
                        AND bsr.created > NOW() - make_interval(secs => :paymentTimeout)
                    RETURNING bsr.id
                    """.trimIndent()
                )
            query.bind("paymentId", paymentId)
            query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
            query.bind("updatedTime", LocalDate.now())
            query.mapTo<Int>().findOne().orElse(null)
        }

    override fun getUnfinishedReservationForCitizen(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.acting_citizen_id = :id
                        AND bsr.status = 'Info' 
                        AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getUnfinishedReservationForEmployee(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.employee_id = :id
                        AND bsr.status = 'Info' 
                        AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationWithReserver(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, r.name, r.type as reserver_type, r.email, r.phone, 
                        location.name as location_name, price.price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description
                    FROM boat_space_reservation bsr
                    LEFT JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment')
                        AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationWithoutReserver(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, location.name as location_name, price.price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description,
                        ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment')
                        AND bsr.created > NOW() - make_interval(secs => :sessionTimeInSeconds)
                    GROUP BY bsr.id, location.name, price.price_cents, bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun removeBoatSpaceReservation(
        id: Int,
        reserverId: UUID,
    ): Unit =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createUpdate(
                    """
                    DELETE FROM boat_space_reservation
                    WHERE id = :id AND reserver_id = :reserverId
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("reserverId", reserverId)
            query.execute()
        }

    override fun getBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.id,
                           bsr.start_date,
                           bsr.end_date,
                           bsr.created,
                           bsr.updated,
                           bsr.status,
                           bsr.boat_space_id,
                           r.id as reserver_id,
                           r.type as reserver_type,
                           r.name,
                           r.email, 
                           r.phone,
                           r.street_address,
                           r.postal_code,
                           r.municipality_code,
                           m.name as municipality_name,
                           b.registration_code as boat_registration_code,
                           b.ownership as boat_ownership,
                           b.id as boat_id,
                           b.name as boat_name,
                           b.width_cm as boat_width_cm,
                           b.length_cm as boat_length_cm,
                           b.weight_kg as boat_weight_kg,
                           b.depth_cm as boat_depth_cm,
                           b.type as boat_type,
                           b.other_identification as boat_other_identification,
                           b.extra_information as boat_extra_information,
                           location.name as location_name, 
                           bs.type,
                           bs.length_cm as boat_space_length_cm,
                           bs.width_cm as boat_space_width_cm,
                           bs.amenity,
                           price.price_cents,
                           CONCAT(bs.section, bs.place_number) as place
                    FROM boat_space_reservation bsr
                    JOIN boat b ON b.id = bsr.boat_id
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location.id = bs.location_id
                    JOIN price ON price_id = price.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE c.id = :reserverId AND 
                        (bsr.status = 'Confirmed' OR bsr.status = 'Invoiced')
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)

            // read warnings that are associated with the reservation
            val reservations = query.mapTo<BoatSpaceReservationDetails>().list()
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
                val warnings = warningQuery.mapTo<String>().list()

                it.copy(
                    warnings = warnings.toSet()
                )
            }
        }

    override fun getBoatSpaceReservation(reservationId: Int): BoatSpaceReservationDetails? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.id,
                           bsr.start_date,
                           bsr.end_date,
                           bsr.created,
                           bsr.updated,
                           bsr.status,
                           bsr.boat_space_id,
                           r.id as reserver_id,
                           r.type as reserver_type,
                           r.name,
                           r.email, 
                           r.phone,
                           r.street_address,
                           r.postal_code,
                           r.municipality_code,
                           m.name as municipality_name,
                           b.registration_code as boat_registration_code,
                           b.ownership as boat_ownership,
                           b.id as boat_id,
                           b.name as boat_name,
                           b.width_cm as boat_width_cm,
                           b.length_cm as boat_length_cm,
                           b.weight_kg as boat_weight_kg,
                           b.depth_cm as boat_depth_cm,
                           b.type as boat_type,
                           b.other_identification as boat_other_identification,
                           b.extra_information as boat_extra_information,
                           location.name as location_name, 
                           bs.type,
                            bs.length_cm as boat_space_length_cm,
                            bs.width_cm as boat_space_width_cm,
                            bs.amenity,
                           price.price_cents,
                           CONCAT(bs.section, bs.place_number) as place
                    FROM boat_space_reservation bsr
                    JOIN boat b ON b.id = bsr.boat_id
                    JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location.id = bs.location_id
                    JOIN price ON price_id = price.id
                    JOIN municipality m ON r.municipality_code = m.code
                    WHERE bsr.id = :reservationId
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.mapTo<BoatSpaceReservationDetails>().findOne().orElse(null)
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

    override fun getBoatSpaceReservations(params: BoatSpaceReservationFilter): List<BoatSpaceReservationItem> =
        jdbi.withHandleUnchecked { handle ->

            var statusFilter =
                params.payment
                    .map {
                        when (it) {
                            PaymentFilter.PAID -> listOf("Confirmed")
                            PaymentFilter.UNPAID -> listOf("Payment", "Invoiced")
                        }
                    }.flatten()

            if (statusFilter.isEmpty()) {
                statusFilter = listOf("Confirmed", "Payment", "Invoiced")
            }

            val nameSearch = buildNameSearchClause(params.nameSearch)

            val warningFilter =
                if (params.warningFilter == true) {
                    "rw.key IS NOT NULL"
                } else {
                    "true"
                }

            val filter =
                AndExpr(
                    listOf(
                        InExpr("bs.location_id", params.harbor),
                        InExpr("bs.amenity", params.amenity) { "'$it'" },
                        InExpr("bsr.status", statusFilter) { "'$it'" },
                        InExpr("bs.section", params.sectionFilter) { "'$it'" }
                    )
                )

            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, r.email, r.phone, r.type as reserver_type, r.name,
                        r.municipality_code,
                        b.registration_code as boat_registration_code,
                        b.ownership as boat_ownership,
                        location.name as location_name, 
                        bs.type, CONCAT(bs.section, bs.place_number) as place,
                        rw.key as warning,
                        bs.section,
                        m.name as municipality_name
                    FROM boat_space_reservation bsr
                    JOIN boat b on b.id = bsr.boat_id
                    JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN municipality m ON r.municipality_code = m.code
                    LEFT JOIN reservation_warning rw ON rw.reservation_id = bsr.id
                    WHERE
                        (bsr.status = 'Confirmed' OR bsr.status = 'Payment' OR bsr.status = 'Invoiced')
                        AND $nameSearch
                        AND $warningFilter
                        AND ${filter.toSql().ifBlank { "true" }}
                    ${getSortingSql(params)}
                    """.trimIndent()
                )

            if (!params.nameSearch.isNullOrBlank()) {
                query.bind("nameSearch", params.nameSearch.trim())
            }

            filter.bind(query)
            query
                .mapTo<BoatSpaceReservationItemWithWarning>()
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
                        boatRegistrationCode = row.boatRegistrationCode,
                        boatOwnership = row.boatOwnership,
                        warnings = (warnings.mapNotNull { it.warning }).toSet(),
                        actingUserId = null,
                        reserverType = row.reserverType,
                        municipalityCode = row.municipalityCode,
                        municipalityName = row.municipalityName,
                    )
                }
        }

    override fun insertBoatSpaceReservation(
        reserverId: UUID,
        actingUserId: UUID?,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO boat_space_reservation (reserver_id, acting_citizen_id, boat_space_id, start_date, end_date)
                    VALUES (:reserverId, :actingUserId, :boatSpaceId, :startDate, :endDate)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("actingUserId", actingUserId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun insertBoatSpaceReservationAsEmployee(
        employeeId: UUID,
        boatSpaceId: Int,
        startDate: LocalDate,
        endDate: LocalDate,
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    INSERT INTO boat_space_reservation (employee_id, boat_space_id, start_date, end_date)
                    VALUES (:employeeId, :boatSpaceId, :startDate, :endDate)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("employeeId", employeeId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateBoatInBoatSpaceReservation(
        reservationId: Int,
        boatId: Int,
        reserverId: UUID,
        reservationStatus: ReservationStatus
    ): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = :status, updated = :updatedTime, boat_id = :boatId, reserver_id = :reserverId
                    WHERE id = :id
                        AND (status = 'Info' OR status = 'Payment')
                        AND created > NOW() - make_interval(secs => :sessionTimeInSeconds)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("status", reservationStatus)
            query.bind("updatedTime", LocalDate.now())
            query.bind("id", reservationId)
            query.bind("boatId", boatId)
            query.bind("reserverId", reserverId)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun setReservationStatusToPayment(reservationId: Int): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = 'Payment', updated = :updatedTime
                    WHERE id = :reservationId
                        AND status = 'Payment'
                        AND created > NOW() - make_interval(secs => :paymentTimeout)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reservationId", reservationId)
            query.bind("updatedTime", LocalDate.now())
            query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun updateReservationInvoicePaid(reservationId: Int): BoatSpaceReservation =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET status = 'Confirmed'
                    WHERE id = :id
                        AND status = 'Invoiced'
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", reservationId)
            query.bind("paymentTimeout", BoatSpaceConfig.PAYMENT_TIMEOUT)
            query.mapTo<BoatSpaceReservation>().one()
        }

    override fun getReservationPeriod(id: String): ReservationPeriod? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT *
                    FROM reservation_period
                    WHERE id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            query.mapTo<ReservationPeriod>().findOne().orElse(null)
        }
}
