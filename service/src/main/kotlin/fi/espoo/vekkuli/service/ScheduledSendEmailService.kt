package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.Recipient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.model.*
import java.util.*

@Service
class ScheduledSendEmailService(
    private val templateEmailService: TemplateEmailService,
    private val reservationService: BoatReservationService,
    private val emailEnv: EmailEnv,
) {
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun sendReservationRenewReminderEmails() {
        val expiringIndefiniteReservations = reservationService.getExpiringIndefiniteBoatSpaceReservations()
        expiringIndefiniteReservations.forEach { reservation ->
            val recipients = listOf(Recipient(reservation.reserverId, reservation.email))
            val sender = emailEnv.senderAddress
            templateEmailService.sendBatchEmail(
                "boat_reservation_renew_reminder",
                null,
                sender,
                recipients,
                mapOf(
                    "name" to "${reservation.locationName} ${reservation.place}",
                    "endDate" to reservation.endDate,
                    "sender" to sender,
                )
            )
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    fun sendReservationExpiryReminderEmails() {
        val expiringFixedTermReservations = reservationService.getExpiringIndefiniteBoatSpaceReservations()
        expiringFixedTermReservations.forEach { reservation ->
            val recipients = listOf(Recipient(reservation.reserverId, reservation.email))
            val sender = emailEnv.senderAddress
            templateEmailService.sendBatchEmail(
                "boat_reservation_expiry_reminder",
                null,
                sender,
                recipients,
                mapOf(
                    "name" to "${reservation.locationName} ${reservation.place}",
                    "endDate" to reservation.endDate,
                    "sender" to sender,
                )
            )
        }
    }
}
