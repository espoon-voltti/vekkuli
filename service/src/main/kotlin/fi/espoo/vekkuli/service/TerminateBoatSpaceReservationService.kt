package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TerminateBoatSpaceReservationService(
    private val boatSpaceReservationRepository: BoatSpaceReservationRepository,
    private val emailService: TemplateEmailService,
    private val reservationService: BoatReservationService,
    private val emailEnv: EmailEnv,
    private val timeProvider: TimeProvider
) {
    @Transactional
    fun terminateBoatSpaceReservation(
        reservationId: Int,
        terminator: CitizenWithDetails
    ) {
        // @TODO check whether user is allowed to terminate the reservation
        // @TODO handle failures
        val reservation = boatSpaceReservationRepository.terminateBoatSpaceReservation(reservationId)
        sendTerminationNotice(reservation, terminator)
    }

    private fun sendTerminationNotice(
        reservation: BoatSpaceReservation,
        terminator: CitizenWithDetails,
    ) {
        sendTerminationNoticeForReserverAndTerminator(reservation, terminator)
        sendTerminationNoticeForEmployee(reservation, terminator)
    }

    private fun sendTerminationNoticeForReserverAndTerminator(
        reservation: BoatSpaceReservation,
        terminator: CitizenWithDetails,
    ) {
        val reservationWithDetails = reservationService.getBoatSpaceReservation(reservation.id) ?: return

        val contactDetails = mutableListOf<Recipient>()
        contactDetails.addAll(reservationService.getContactDetailsForReservation(reservation.id))

        if (contactDetails.none { it.id == terminator.id }) {
            contactDetails.add(Recipient(terminator.id, terminator.email))
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

    private fun sendTerminationNoticeForEmployee(
        reservation: BoatSpaceReservation,
        terminator: CitizenWithDetails
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
                    "terminator" to terminator.fullName,
                    "time" to timeProvider.getCurrentDateTime(),
                    "location" to reservationWithDetails.locationName,
                    "place" to reservationWithDetails.place
                )
            )
    }
}
