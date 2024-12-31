package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.ReservationValidity
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
class BoatSpaceSwitchRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun getSwitchReservationForCitizen(
        id: UUID,
        reservationId: Int
    ): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSelectForReservationWithDependencies()}
                    WHERE bsr.acting_citizen_id = :id AND bsr.original_reservation_id = :reservationId AND bsr.creation_type = 'Switch' AND bsr.status = 'Info'
                    AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", id)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("reservationId", reservationId)
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun getSwitchReservationForEmployee(
        employeeId: UUID,
        originalReservationId: Int
    ): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSelectForReservationWithDependencies()}
                    WHERE bsr.employee_id = :id AND bsr.original_reservation_id = :reservationId AND bsr.creation_type = 'Switch' AND 
                    bsr.status = 'Info' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", employeeId)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("reservationId", originalReservationId)
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun createSwitchRow(
        originalReservationId: Int,
        userType: UserType,
        userId: UUID,
        boatSpaceId: Int,
        endDate: LocalDate,
        validity: ReservationValidity
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
                      original_reservation_id,
                      storage_type,
                      trailer_id,
                      creation_type
                    )
                    (
                      SELECT :created as created,
                             reserver_id, 
                             :actingCitizenId as acting_citizen_id, 
                             :boatSpaceId as boat_space_id, 
                             start_date, 
                             :endDate as end_date, 
                             'Info' as status, 
                             :validity as validity, 
                             boat_id, 
                             :employeeId as employee_id,
                             id as original_reservation_id,
                             storage_type,
                             trailer_id,
                             'Switch' as creation_type
                      FROM boat_space_reservation
                      WHERE id = :reservationId
                    )
                    RETURNING id
                    """.trimIndent()
                ).bind("created", timeProvider.getCurrentDateTime())
                .bind("reservationId", originalReservationId)
                .bind("actingCitizenId", if (userType == UserType.CITIZEN) userId else null)
                .bind("employeeId", if (userType == UserType.EMPLOYEE) userId else null)
                .bind("boatSpaceId", boatSpaceId)
                .bind("endDate", endDate)
                .bind("validity", validity)
                .mapTo<Int>()
                .one()
        }

    private fun buildSelectForReservationWithDependencies() =
        """SELECT bsr.*, r.name,  r.email, r.phone, r.type as reserver_type,
                location.name as location_name, price.price_cents, price.vat_cents, price.net_price_cents, 
                bs.type, bs.section, bs.place_number, bs.amenity, bs.width_cm, bs.length_cm,
                  bs.description,
                  CONCAT(section, ' ', TO_CHAR(place_number, 'FM000')) as place
            FROM boat_space_reservation bsr
            JOIN reserver r ON bsr.reserver_id = r.id
            JOIN boat_space bs ON bsr.boat_space_id = bs.id
            JOIN location ON location_id = location.id
            JOIN price ON price_id = price.id"""
}
