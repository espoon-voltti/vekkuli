package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*

@Service
class TerminateReservationService(
    private val emailService: TemplateEmailService,
    private val reservationService: BoatReservationService,
    private val emailEnv: EmailEnv,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val reserverService: ReserverService,
    private val terminateReservationRepository: TerminateReservationRepository,
    private val messageService: MessageService
) {
    @Transactional
    fun terminateBoatSpaceReservationAsOwner(
        reservationId: Int,
        terminatorId: UUID
    ) {
        if (!permissionService.canTerminateBoatSpaceReservation(terminatorId, reservationId)) {
            throw Unauthorized()
        }
        val reservation =
            terminateReservationRepository.terminateBoatSpaceReservation(
                reservationId,
                timeProvider.getCurrentDate(),
                ReservationTerminationReason.UserRequest,
                null
            )

        if (reservation == null) {
            throw BadRequest("Reservation not found")
        }

        sendTerminationNotice(reservation, terminatorId)
    }

    @Transactional
    fun terminateBoatSpaceReservationAsEmployee(
        reservationId: Int,
        terminatorId: UUID,
        terminationReason: ReservationTerminationReason,
        endDate: LocalDate,
        comment: String? = null,
        messageTitle: String,
        messageContent: String
    ) {
        if (!permissionService.canTerminateBoatSpaceReservationForOtherUser(terminatorId, reservationId)) {
            throw Unauthorized()
        }
        val reservation =
            terminateReservationRepository.terminateBoatSpaceReservation(
                reservationId,
                endDate,
                terminationReason,
                comment
            )

        if (reservation == null) {
            throw BadRequest("Reservation not found")
        }

        sendCustomTerminationNotice(reservation, terminatorId, messageTitle, messageContent)
    }

    private fun sendTerminationNotice(
        reservation: BoatSpaceReservation,
        terminatorId: UUID,
    ) {
        val citizen = reserverService.getCitizen(terminatorId)
        sendTerminationNoticeForReserverAndTerminator(reservation, citizen)
        if (citizen != null) {
            sendTerminationNoticeToEmployees(reservation, citizen)
        }
    }

    private fun sendTerminationNoticeForReserverAndTerminator(
        reservation: BoatSpaceReservation,
        citizen: CitizenWithDetails?
    ) {
        val reservationWithDetails = reservationService.getBoatSpaceReservation(reservation.id) ?: return
        val reserverContact = reservationService.getEmailRecipientForReservation(reservation.id)
        val contactDetails = mutableListOf<Recipient>()

        if (reserverContact != null) {
            contactDetails.add(reserverContact)
        }

        if (citizen != null && reserverContact?.id != citizen.id) {
            contactDetails.add(Recipient(citizen.id, citizen.email))
        }

        if (contactDetails.isEmpty()) {
            throw BadRequest("No contact details found for reservation")
        }

        emailService
            .sendBatchEmail(
                "reservation_termination_notice_no_refund",
                null,
                emailEnv.senderAddress,
                contactDetails,
                mapOf(
                    "location" to reservationWithDetails.locationName,
                    "place" to reservationWithDetails.place
                )
            )
    }

    private fun sendTerminationNoticeToEmployees(
        reservation: BoatSpaceReservation,
        citizen: CitizenWithDetails
    ) {
        val reservationWithDetails = reservationService.getBoatSpaceReservation(reservation.id) ?: return
        val recipient = Recipient(null, emailEnv.employeeAddress)
        val contactDetails = listOf(recipient)

        emailService
            .sendBatchEmail(
                "marine_reservation_termination_employee_notice",
                null,
                emailEnv.senderAddress,
                contactDetails,
                mapOf(
                    "terminator" to citizen.fullName,
                    "time" to
                        timeProvider.getCurrentDateTime().format(
                            fullDateTimeFormat
                        ),
                    "location" to reservationWithDetails.locationName,
                    "place" to reservationWithDetails.place
                )
            )
    }

    private fun sendCustomTerminationNotice(
        reservation: BoatSpaceReservation,
        terminatorId: UUID,
        subject: String,
        content: String
    ) {
        val reserverRecipient =
            reservationService.getEmailRecipientForReservation(reservation.id)
                ?: throw BadRequest("No contact details found for reservation")

        messageService.sendEmails(
            userId = terminatorId,
            senderAddress = emailEnv.senderAddress,
            recipients = listOf(reserverRecipient),
            subject = subject,
            body = content
        )
    }
}
