package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.boatSpace.reservationForm.ReservationForApplicationForm
import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class RenewalReservationForApplicationForm(
    id: Int,
    reserverId: UUID?,
    boatId: Int?,
    lengthCm: Int,
    widthCm: Int,
    amenity: BoatSpaceAmenity,
    boatSpaceType: BoatSpaceType,
    place: String,
    locationName: String?,
    validity: ReservationValidity?,
    startDate: LocalDate,
    endDate: LocalDate,
    priceCents: Int,
    vatCents: Int,
    netPriceCents: Int,
    created: LocalDateTime,
    excludedBoatTypes: List<BoatType>?,
    section: String,
    placeNumber: String,
    storageType: StorageType?,
    val renewdFromReservationId: String
) : ReservationForApplicationForm(
        id,
        reserverId,
        boatId,
        lengthCm,
        widthCm,
        amenity,
        boatSpaceType,
        place,
        locationName,
        validity,
        startDate,
        endDate,
        priceCents,
        vatCents,
        netPriceCents,
        created,
        excludedBoatTypes,
        section,
        placeNumber,
        storageType
    )

@Repository
class BoatSpaceRenewalRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun getRenewalReservationForCitizen(
        id: UUID,
        reservationId: Int
    ): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSelectForReservationWithDependencies()}
                    WHERE bsr.acting_citizen_id = :id AND bsr.renewed_from_id = :reservationId AND bsr.status = 'Renewal' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("reservationId", reservationId)
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun getRenewalReservationForEmployee(
        id: UUID,
        reservationId: Int
    ): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSelectForReservationWithDependencies()}
                    WHERE bsr.employee_id = :id AND bsr.renewed_from_id = :reservationId AND bsr.status = 'Renewal' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("reservationId", reservationId)
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun createRenewalRow(
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
                      renewed_from_id,
                      storage_type,
                      trailer_id
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
                             id as renewed_from_id,
                             storage_type,
                             trailer_id
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

    private fun buildSelectForReservationWithDependencies() =
        """SELECT bsr.*, c.first_name, c.last_name, r.email, r.phone, 
                location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
                bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                  bs.description,
                  CONCAT(section, ' ', TO_CHAR(place_number, 'FM000')) as place
            FROM boat_space_reservation bsr
            JOIN citizen c ON bsr.reserver_id = c.id 
            JOIN reserver r ON c.id = r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN price ON price_id = price.id"""
}
