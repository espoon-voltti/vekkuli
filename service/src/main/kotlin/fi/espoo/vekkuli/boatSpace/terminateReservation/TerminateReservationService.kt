package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.MessageUtil
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
    private val reservationWarningRepository: ReservationWarningRepository,
    private val emailEnv: EmailEnv,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val reserverService: ReserverService,
    private val terminateReservationRepository: TerminateReservationRepository,
    private val messageService: MessageService,
    private val organizationService: OrganizationService,
    private val messageUtil: MessageUtil
) {
    @Transactional
    fun terminateBoatSpaceReservationAsOwner(
        reservationId: Int,
        terminatorId: UUID
    ) {
        if (!permissionService.canTerminateBoatSpaceReservation(terminatorId, reservationId)) {
            throw Unauthorized()
        }

        val reservation = reservationService.getBoatSpaceReservation(reservationId) ?: throw BadRequest("Reservation not found")

        val terminatedReservation =
            when (reservation.type) {
                BoatSpaceType.Trailer,
                BoatSpaceType.Slip ->
                    executeBoatSpaceReservationTerminationAsOwner(
                        reservationId,
                        timeProvider.getCurrentDate()
                    )
                BoatSpaceType.Storage, BoatSpaceType.Winter ->
                    executeBoatSpaceReservationTerminationAsOwner(
                        reservationId,
                        reservation.endDate
                    )
            }
        reservationWarningRepository.deleteReservationWarningsForReservation(reservation.id)

        sendTerminationNotice(terminatedReservation, terminatorId)
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

        reservationWarningRepository.deleteReservationWarningsForReservation(reservation.id)

        sendCustomTerminationNotice(reservation, terminatorId, messageTitle, messageContent)
    }

    private fun executeBoatSpaceReservationTerminationAsOwner(
        reservationId: Int,
        endDate: LocalDate,
        comment: String? = null
    ): BoatSpaceReservation {
        val reservation =
            terminateReservationRepository.terminateBoatSpaceReservation(
                reservationId,
                endDate,
                ReservationTerminationReason.UserRequest,
                comment
            )
                ?: throw BadRequest("Reservation not found")

        return reservation
    }

    private fun sendTerminationNotice(
        reservation: BoatSpaceReservation,
        terminatorId: UUID,
    ) {
        val terminator = reserverService.getCitizen(terminatorId)
        sendTerminationNoticeForReserverAndTerminator(reservation, terminator)
        if (terminator != null) { // when would the terminator be null??
            sendTerminationNoticeToEmployees(reservation, terminator)
        }
    }

    private fun sendTerminationNoticeForReserverAndTerminator(
        reservation: BoatSpaceReservation,
        terminator: CitizenWithDetails?
    ) {
        val reservationWithDetails =
            reservationService.getBoatSpaceReservation(reservation.id)
                ?: throw BadRequest("Reservation ${reservation.id} not found")
        val reserverContact = reservationService.getEmailRecipientForReservation(reservation.id)
        val contactDetails = mutableListOf<Recipient>()

        // person who originally reserved the space
        if (reserverContact != null) {
            contactDetails.add(reserverContact)
        }

        // if person terminating the reservation is different from original reserver, add he/she
        if (terminator != null && reserverContact?.id != terminator.id) {
            contactDetails.add(Recipient(terminator.id, terminator.email))
        }

        val orgRecipients =
            if (reservationWithDetails.reserverType == ReserverType.Organization) {
                organizationService
                    .getOrganizationMembers(reservationWithDetails.reserverId)
                    .filter { it.id != terminator?.id && it.id != reserverContact?.id }
                    .map { Recipient(it.id, it.email) }
            } else {
                emptyList()
            }

        // add possible organization members
        contactDetails.addAll(orgRecipients)

        if (contactDetails.isEmpty()) {
            throw BadRequest("No contact details found for reservation")
        }

        val reservationDescription = "${reservationWithDetails.locationName} ${reservationWithDetails.place}"

        emailService
            .sendBatchEmail(
                "reservation_termination_by_citizen",
                null,
                emailEnv.senderAddress,
                contactDetails,
                mapOf(
                    "name" to reservationDescription,
                    "reserverName" to reservationWithDetails.name,
                    "terminatorName" to (terminator?.fullName ?: "")
                ) +
                    messageUtil.getLocalizedMap(
                        "placeType",
                        "boatSpaceReservation.email.types.${reservationWithDetails.type}"
                    )
            )
    }

    private fun sendTerminationNoticeToEmployees(
        reservation: BoatSpaceReservation,
        terminator: CitizenWithDetails
    ) {
        val reservationWithDetails = reservationService.getBoatSpaceReservation(reservation.id) ?: return
        val recipient = Recipient(null, emailEnv.employeeAddress)
        val contactDetails = listOf(recipient)
        val reservationDescription = "${reservationWithDetails.locationName} ${reservationWithDetails.place}"

        emailService
            .sendBatchEmail(
                "reservation_termination_by_citizen_to_employee",
                null,
                emailEnv.senderAddress,
                contactDetails,
                mapOf(
                    "name" to reservationDescription,
                    "reserverName" to reservationWithDetails.name,
                    "reserverEmail" to reservationWithDetails.email,
                    "terminatorName" to terminator.fullName,
                    "terminatorEmail" to terminator.email,
                    "terminatorPhone" to terminator.phone,
                    "time" to
                        timeProvider.getCurrentDateTime().format(
                            fullDateTimeFormat
                        ),
                ) +
                    messageUtil.getLocalizedMap(
                        "placeType",
                        "boatSpaceReservation.email.types.${reservationWithDetails.type}"
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
