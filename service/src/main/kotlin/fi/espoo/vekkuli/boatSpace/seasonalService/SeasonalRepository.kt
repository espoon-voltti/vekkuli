package fi.espoo.vekkuli.boatSpace.seasonalService

import fi.espoo.vekkuli.domain.ReservationPeriod
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository

@Repository
class SeasonalRepository(
    private val jdbi: Jdbi
) {
    fun getReservationPeriods(): List<ReservationPeriod> =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    SELECT
                        start_date,
                        end_date,
                        operation,
                        boat_space_type,
                        is_espoo_citizen
                    FROM booking_period
                    """.trimIndent()
                )
            query.mapTo<ReservationPeriod>().list()
        }
}
