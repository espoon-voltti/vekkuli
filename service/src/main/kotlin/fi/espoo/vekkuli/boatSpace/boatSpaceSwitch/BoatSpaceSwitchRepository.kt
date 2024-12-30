package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.controllers.UserType
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

class data

@Repository
class BoatSpaceSwitchRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun getSwitchReservationForEmployee(
        actingCitizenId: UUID,
        originalReservationId: Int
    ): ReservationWithDependencies? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    ${buildSelectForReservationWithDependencies()}
                    WHERE bsr.acting_citizen_id = :id AND bsr.original_reservation_id = :reservationId AND bsr.status = 'Renewal' 
                        AND bsr.created > :currentTime - make_interval(secs => :sessionTimeInSeconds)
                    """.trimIndent()
                )
            query.bind("id", actingCitizenId)
            query.bind("sessionTimeInSeconds", BoatSpaceConfig.SESSION_TIME_IN_SECONDS)
            query.bind("currentTime", timeProvider.getCurrentDateTime())
            query.bind("reservationId", originalReservationId)
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun getSwitchReservationForCitizen(
        userId: UUID,
        originalReservationId: Int
    ): ReservationWithDependencies = throw NotImplementedError()

    fun createSwitchRow(
        originalReservationId: Int,
        userType: UserType,
        userId: UUID
    ): Int = throw NotImplementedError()

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
