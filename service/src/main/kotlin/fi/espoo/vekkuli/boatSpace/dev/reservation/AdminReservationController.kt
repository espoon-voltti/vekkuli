package fi.espoo.vekkuli.boatSpace.dev.reservation

import fi.espoo.vekkuli.controllers.EnvType
import fi.espoo.vekkuli.controllers.Utils.Companion.getEnv
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/dev")
class AdminReservationController {
    @Autowired
    lateinit var reservationView: ReservationView

    @Autowired
    lateinit var jdbi: Jdbi

    @PostMapping("/reservations")
    fun clearReservations(
        @RequestParam reserverName: String,
        @RequestParam user: UserColumn,
    ): String {
        if (getEnv() == EnvType.Production) {
            return ""
        }

        val nameSearch = "%$reserverName%"
        val query =
            when (user) {
                UserColumn.Reserver -> """SELECT id FROM reserver WHERE (name ILIKE :reserverName)"""
                UserColumn.ActingUser -> """SELECT id FROM reserver WHERE (name ILIKE :reserverName)"""
                UserColumn.Employee -> """SELECT id FROM app_user WHERE (CONCAT(last_name, ' ', first_name) ILIKE :reserverName)"""
            }

        val reservers =
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createQuery(query)
                    .bind("reserverName", nameSearch)
                    .mapTo<UUID>()
                    .list()
            }

        return when (reservers.size) {
            0 -> reservationView.render("Varaajaa ei löytynyt")
            1 -> reservationView.render(deleteReservations(reservers[0], user))
            else ->
                reservationView.render("Varaajia löytyi useita. Tarkenna nimeä.")
        }
    }

    fun deleteReservations(
        userId: UUID,
        userColumn: UserColumn
    ): String {
        val query = "SELECT id FROM boat_space_reservation WHERE ${userColumn.toName()} = :userId"
        return jdbi.withHandleUnchecked { handle ->
            // Read all reservation ids for the reserver
            val reservationIds =
                handle
                    .createQuery(
                        query,
                    ).bind("userId", userId)
                    .mapTo<Int>()
                    .list()

            if (reservationIds.isEmpty()) return@withHandleUnchecked "Ei löytynyt varauksia henkilölle $userId"

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

            "Henkilöltä $userId poistettu seuraavat varaukset: $ids"
        }
    }
}
