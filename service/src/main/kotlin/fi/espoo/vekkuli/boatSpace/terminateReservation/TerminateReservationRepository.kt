package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class TerminateReservationRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun terminateBoatSpaceReservation(
        reservationId: Int,
        endDate: LocalDate,
        terminationReason: ReservationTerminationReason,
        terminationComment: String?
    ): BoatSpaceReservation? =
        jdbi.withHandleUnchecked { handle ->
            val query =
                handle.createQuery(
                    """
                    UPDATE boat_space_reservation
                    SET 
                        status = 'Cancelled', 
                        updated = :updatedTimestamp, 
                        end_date = :endDate,
                        termination_reason = :terminationReason,
                        termination_comment = :terminationComment
                    WHERE id = :id
                        AND status <> 'Cancelled'
                    RETURNING *
                    """.trimIndent()
                )
            query.bind("id", reservationId)
            query.bind("updatedTimestamp", timeProvider.getCurrentDateTime())
            query.bind("endDate", endDate)
            query.bind("terminationReason", terminationReason)
            query.bind("terminationComment", terminationComment)
            query.mapTo<BoatSpaceReservation>().one()
        }
}
