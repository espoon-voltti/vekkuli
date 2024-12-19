package fi.espoo.vekkuli.boatSpace.admin.reservation

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/admin")
class ReservationController {
    @Autowired
    private lateinit var boatSpaceInvoiceService: BoatSpaceInvoiceService

    @Autowired
    lateinit var reservationView: ReservationView

    @Autowired
    lateinit var jdbi: Jdbi

    @PostMapping("/reservations")
    fun clearReservations(
        @RequestParam reserverName: String
    ): String {
        val reservers =
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createQuery("""SELECT id FROM reserver WHERE (name ILIKE '%$reserverName%')""")
                    .mapTo<UUID>()
                    .list()
            }
        return when (reservers.size) {
            0 -> reservationView.render("Varaajaa ei löytynyt")
            1 -> {
                deleteReservations(reservers[0])
                reservationView.render("Varaukset poistettu henkilöltä")
            }
            else ->
                reservationView.render("Varaajia löytyi useita. Tarkenna nimeä.")
        }
    }

    fun deleteReservations(reserverId: UUID) {
        jdbi.withHandleUnchecked { handle ->
            // Read all reservation ids for the reserver
            val reservationIds =
                handle
                    .createQuery(
                        "SELECT id FROM boat_space_reservation WHERE reserver_id = :reserverId",
                    ).bind("reserverId", reserverId)
                    .mapTo<Int>()
                    .list()

            val ids = reservationIds.joinToString(", ")
            // Delete invoices
            handle
                .createUpdate(
                    """    
                        DELETE FROM invoice WHERE reservation_id IN ($ids)
                    """
                ).execute()

            // Delete payments
            handle
                .createUpdate(
                    """    
                        DELETE FROM payment WHERE reservation_id IN ($ids)
                    """
                ).execute()

            // Delete warnings
            handle
                .createUpdate(
                    """    
                        DELETE FROM reservation_warning WHERE reservation_id IN ($ids)
                    """
                ).execute()

            // Delete reservations
            handle
                .createUpdate(
                    "DELETE FROM boat_space_reservation WHERE id IN ($ids)",
                ).execute()
        }
    }
}
