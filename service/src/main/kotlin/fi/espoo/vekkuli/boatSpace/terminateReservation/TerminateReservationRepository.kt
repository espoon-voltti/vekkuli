package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TerminateReservationRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun terminateBoatSpaceReservation(
        reservationId: Int,
        endDate: LocalDateTime,
        terminationReason: ReservationTerminationReason,
        terminationComment: String?
    ): BoatSpaceReservation? {
        val res =
            jdbi.withHandleUnchecked { handle ->
                val query =
                    handle.createQuery(
                        """
                        UPDATE boat_space_reservation
                        SET 
                            status = 'Cancelled', 
                            updated = :nowTimestamp, 
                            end_date = :endDate,
                            termination_reason = :terminationReason,
                            termination_comment = :terminationComment,
                            termination_timestamp = :nowTimestamp
                        WHERE id = :id
                            AND status <> 'Cancelled'
                        RETURNING *
                        """.trimIndent()
                    )
                query.bind("id", reservationId)
                query.bind("nowTimestamp", timeProvider.getCurrentDateTime())
                query.bind("endDate", endDate)
                query.bind("terminationReason", terminationReason)
                query.bind("terminationComment", terminationComment)
                query.mapTo<BoatSpaceReservation>().one()
            }
        return res
    }
}
