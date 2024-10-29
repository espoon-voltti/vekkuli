package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.BoatSpaceReservationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class TerminateBoatSpaceReservationService {
    @Autowired lateinit var boatSpaceReservationRepository: BoatSpaceReservationRepository

    @Autowired lateinit var emailService: TemplateEmailService

    @Autowired lateinit var reservationService: BoatReservationService

    @Autowired lateinit var emailEnv: EmailEnv

    @Transactional
    fun terminateBoatSpaceReservation(
        reservationId: Int,
        terminator: CitizenWithDetails
    ) {
        // @TODO check wether user is allowed to terminate tha reservation
        val reservation = boatSpaceReservationRepository.terminateBoatSpaceReservation(reservationId)
        sendTerminationNotice(reservation, terminator)
    }

    private fun sendTerminationNotice(
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
}
