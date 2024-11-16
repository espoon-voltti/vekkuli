package fi.espoo.vekkuli.boatSpace.terminateReservation

import fi.espoo.vekkuli.common.BadRequest
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.service.PermissionService
import fi.espoo.vekkuli.service.TemplateEmailService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.fullDateTimeFormat
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class TerminateBoatSpaceReservationService(
    private val emailService: TemplateEmailService,
    private val reservationService: BoatReservationService,
    private val emailEnv: EmailEnv,
    private val timeProvider: TimeProvider,
    private val permissionService: PermissionService,
    private val citizenService: CitizenService,
    private val terminateReservationRepository: TerminateReservationRepository
) {
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

    fun terminateBoatSpaceReservationForOtherUser(
        reservationId: Int,
        terminatorId: UUID,
        terminationReason: ReservationTerminationReason,
        endDate: LocalDate,
        comment: String?
    ) {
        if (!permissionService.canTerminateBoatSpaceReservation(terminatorId, reservationId)) {
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
    }

    private fun sendTerminationNotice(
        reservation: BoatSpaceReservation,
        terminatorId: UUID,
    ) {
        val citizen = citizenService.getCitizen(terminatorId)
        sendTerminationNoticeForReserverAndTerminator(reservation, citizen)
        if (citizen != null) {
            sendTerminationNoticeForEmployee(reservation, citizen)
        }
    }

    private fun sendTerminationNoticeForReserverAndTerminator(
        reservation: BoatSpaceReservation,
        citizen: CitizenWithDetails?
    ) {
        val reservationWithDetails = reservationService.getBoatSpaceReservation(reservation.id) ?: return

        val contactDetails = mutableListOf<Recipient>()
        contactDetails.addAll(reservationService.getContactDetailsForReservation(reservation.id))

        if (citizen != null && contactDetails.none { it.id == citizen.id }) {
            contactDetails.add(Recipient(citizen.id, citizen.email))
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
}

data class TerminateBoatSpaceReservationOptions(
    val reservationId: Int,
    val terminatorId: UUID,
    val reason: ReservationTerminationReason,
    val endDate: LocalDate,
    val explanation: String? = "",
)
