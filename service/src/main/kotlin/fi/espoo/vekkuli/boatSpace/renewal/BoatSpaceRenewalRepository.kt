package fi.espoo.vekkuli.boatSpace.renewal

import fi.espoo.vekkuli.config.BoatSpaceConfig
import fi.espoo.vekkuli.domain.ReservationWithDependencies
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class BoatSpaceRenewalRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun getRenewalReservationForCitizen(id: UUID): ReservationWithDependencies? =
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
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }

    fun getRenewalReservationForEmployee(id: UUID): ReservationWithDependencies? =
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
            query.mapTo<ReservationWithDependencies>().findOne()?.orElse(null)
        }
}
