package fi.espoo.vekkuli.boatSpace.boatSpaceSwitch

import fi.espoo.vekkuli.domain.BoatSpaceReservation
import fi.espoo.vekkuli.utils.TimeProvider
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class BoatSpaceSwitchRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) {
    fun copyReservationToSwitchReservation(
        originalReservationId: Int,
        actingCitizenId: UUID,
        boatSpaceId: Int,
    ): BoatSpaceReservation =
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
                             end_date, 
                             'Info' as status, 
                             validity, 
                             boat_id, 
                             id as original_reservation_id,
                             storage_type,
                             trailer_id,
                             'Switch' as creation_type
                      FROM boat_space_reservation
                      WHERE id = :reservationId
                    )
                    RETURNING *
                    """.trimIndent()
                ).bind("created", timeProvider.getCurrentDateTime())
                .bind("reservationId", originalReservationId)
                .bind("actingCitizenId", actingCitizenId)
                .bind("boatSpaceId", boatSpaceId)
                .mapTo<BoatSpaceReservation>()
                .one()
        }
}
