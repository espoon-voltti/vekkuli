package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.filter.boatspacereservation.BoatSpaceReservationSortBy
import fi.espoo.vekkuli.utils.SqlExpr
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

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
                           bsr.validity,
                           bsr.renewed_from_id,
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
                           price.vat_cents,
                           price.net_price_cents,
                           CONCAT(bs.section, TO_CHAR(bs.place_number, 'FM000')) as place,
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
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.acting_citizen_id = :id
                        AND bsr.status = 'Info' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
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
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents,
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
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

    override fun getRenewalReservationForCitizen(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents,
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.acting_citizen_id = :id AND bsr.status = 'Renewal' 
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getRenewalReservationForEmployee(id: UUID): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents,
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN citizen c ON bsr.reserver_id = c.id 
                    JOIN reserver r ON c.id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.employee_id = :id AND bsr.status = 'Renewal' 
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationWithReserver(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, r.name, r.type as reserver_type, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    LEFT JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment' OR bsr.status = 'Renewal')
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationWithDependencies(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, r.name, r.type as reserver_type, r.email, r.phone, 
                        location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    LEFT JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
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
                    SELECT bsr.*, r.name, r.type as reserver_type, r.email, r.phone, 
                        location.name as location_name, price.price_cents, 
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                          bs.description,
                          CONCAT(section, TO_CHAR(place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    LEFT JOIN reserver r ON bsr.reserver_id = r.id
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    WHERE bsr.id = :id
                    """.trimIndent()
                )
            query.bind("id", id)
            query.mapTo<ReservationWithDependencies>().findOne().orElse(null)
        }

    override fun getReservationWithoutReserver(id: Int): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT bsr.*, location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents,
                        bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description,
                        ARRAY_AGG(harbor_restriction.excluded_boat_type) as excluded_boat_types,
                        CONCAT(bs.section, TO_CHAR(bs.place_number, 'FM000')) as place
                    FROM boat_space_reservation bsr
                    JOIN boat_space bs ON bsr.boat_space_id = bs.id
                    JOIN location ON location_id = location.id
                    JOIN price ON price_id = price.id
                    LEFT JOIN harbor_restriction ON harbor_restriction.location_id = bs.location_id
                    WHERE bsr.id = :id
                        AND (bsr.status = 'Info' OR bsr.status = 'Payment' OR bsr.status = 'Renewal')
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    GROUP BY bsr.id, location.name, price.id, bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm, bs.description
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
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

    override fun getBoatSpaceReservationsForCitizen(
        reserverId: UUID,
        spaceType: BoatSpaceType
    ): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectForBoatSpaceReservationDetails()}
                    WHERE r.id = :reserverId AND 
                      bs.type = :spaceType AND
                        (bsr.status = 'Confirmed' OR bsr.status = 'Invoiced') AND
                        bsr.end_date >= :endDateCut
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("spaceType", spaceType)
            query.bind("endDateCut", timeProvider.getCurrentDate())

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
                    ${buildSqlSelectForBoatSpaceReservationDetails()}
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

    override fun getBoatSpaceReservations(
        filter: SqlExpr,
        sortBy: BoatSpaceReservationSortBy?
    ) = jdbi.withHandleUnchecked { handle ->
        val filterQuery = filter.toSql()
        val sortByQuery = sortBy?.apply()?.takeIf { it.isNotEmpty() } ?: ""
        val query =
            handle.createQuery(
                """
                SELECT bsr.*, r.email, r.phone, r.type as reserver_type, r.name,
                    r.municipality_code,
                    b.registration_code as boat_registration_code,
                    b.ownership as boat_ownership,
                    location.name as location_name, 
                    bs.type, bs.place_number, 
                    CONCAT(bs.section, TO_CHAR(bs.place_number, 'FM000')) as place,
                    rw.key as warning,
                    bs.section,
                    m.name as municipality_name,
                    p.created as payment_date
                FROM boat_space_reservation bsr
                JOIN boat b on b.id = bsr.boat_id
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
                    paymentDate = row.paymentDate
                )
            }
    }

    override fun createRenewalRow(
        reservationId: Int,
        userType: UserType,
        userId: UUID
    ): Int =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
                    INSERT INTO boat_space_reservation (
                      created,
                      reserver_id, 
                      acting_citizen_id, 
                      boat_space_id, 
                      start_date, 
                      end_date, 
                      status, 
                      validity, 
                      boat_id, 
                      employee_id,
                      renewed_from_id
                    )
                    (
                      SELECT :created as created,
                             reserver_id, 
                             :actingCitizenId as acting_citizen_id, 
                             boat_space_id, 
                             start_date, 
                             (end_date + INTERVAL '1 year') as end_date, 'Renewal' as status, 
                             validity, 
                             boat_id, 
                             :employeeId as employee_id,
                             id as renewed_from_id
                      FROM boat_space_reservation
                      WHERE id = :reservationId
                    )
                    RETURNING id
                    """.trimIndent()
                ).bind("created", timeProvider.getCurrentDateTime())
                .bind("reservationId", reservationId)
                .bind("actingCitizenId", if (userType == UserType.CITIZEN) userId else null)
                .bind("employeeId", if (userType == UserType.EMPLOYEE) userId else null)
                .mapTo<Int>()
                .one()
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
                    INSERT INTO boat_space_reservation (reserver_id, acting_citizen_id, boat_space_id, start_date, end_date, created)
                    VALUES (:reserverId, :actingUserId, :boatSpaceId, :startDate, :endDate, :currentDate)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("reserverId", reserverId)
            query.bind("actingUserId", actingUserId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.bind("currentDate", timeProvider.getCurrentDateTime())
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
                    INSERT INTO boat_space_reservation (employee_id, boat_space_id, start_date, end_date, created)
                    VALUES (:employeeId, :boatSpaceId, :startDate, :endDate, :currentDate)
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("employeeId", employeeId)
            query.bind("boatSpaceId", boatSpaceId)
            query.bind("startDate", startDate)
            query.bind("endDate", endDate)
            query.bind("currentDate", timeProvider.getCurrentDateTime())
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
                        AND (status = 'Info' OR status = 'Payment' OR status = 'Renewal')
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
                        AND status = 'Payment'
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
                        AND status = 'Invoiced'
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", reservationId)
            query.bind("updatedTime", timeProvider.getCurrentDateTime())
            query.mapTo<BoatSpaceReservation>().singleOrNull()
        }

    override fun getReservationPeriods(): List<ReservationPeriod> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT * FROM reservation_period
                    """.trimIndent()
                )
            query.mapTo<ReservationPeriod>().list()
        }

    override fun getExpiredBoatSpaceReservationsForCitizen(reserverId: UUID): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                     ${buildSqlSelectForBoatSpaceReservationDetails()}
                    WHERE r.id = :reserverId AND (
                        bsr.status = 'Cancelled'
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

    override fun getExpiringBoatSpaceReservations(validity: ReservationValidity): List<BoatSpaceReservationDetails> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSqlSelectForBoatSpaceReservationDetails()}
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

    private fun buildSqlSelectForBoatSpaceReservationDetails() =
        """SELECT bsr.id,
                bsr.start_date,
                bsr.end_date,
                bsr.created,
                bsr.updated,
                bsr.status,
                bsr.boat_space_id,
                bsr.validity,
                bsr.renewed_from_id,
                bsr.termination_reason,
                bsr.termination_comment,
                p.id as payment_id,
                           p.paid as payment_date,r.id as reserver_id,
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
                price.vat_cents,
                price.net_price_cents,
                CONCAT(bs.section, TO_CHAR(bs.place_number, 'FM000')) as place
            FROM boat_space_reservation bsr
            JOIN boat b ON b.id = bsr.boat_id
            JOIN reserver r ON bsr.reserver_id =  r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location.id = bs.location_id
            LEFT JOIN price ON price_id = price.id
            JOIN municipality m ON r.municipality_code = m.code
            LEFT JOIN payment p ON p.reservation_id = bsr.id AND p.status = 'Success'
                    """
}
