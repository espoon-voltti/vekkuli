package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.boatSpace.employeeReservationList.PaginatedReservationsResult
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.SendMessageView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.BoatSpaceReservationItem
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.service.MessageService
import fi.espoo.vekkuli.service.OrganizationService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/virkailija/viestit")
class MessageSendingController(
    private val sendMessageView: SendMessageView,
    private val messageService: MessageService,
    private var reservationListService: EmployeeReservationListService,
    private val organizationService: OrganizationService,
    private val emailEnv: EmailEnv,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/massa/modal")
    @ResponseBody
    fun sendMessageModal(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser()
        authenticatedUser?.let {
            logger.audit(
                it,
                "OPEN_SEND_MASS_EMAILS_MODAL",
            )
        }
        if (authenticatedUser?.isEmployee() != true) {
            throw Unauthorized()
        }

        val reservations =
            reservationListService.getBoatSpaceReservations(params)
        val recipients = getDistinctRecipients(reservations)
        return ResponseEntity.ok(
            sendMessageView.renderSendMessageModal(reservations.totalRows, recipients.size)
        )
    }

    @PostMapping("/massa/laheta")
    @ResponseBody
    fun sendMassEmails(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        @RequestParam("messageTitle") messageTitle: String,
        @RequestParam("messageContent") messageContent: String,
    ): ResponseEntity<String> {
        request.getAuthenticatedUser()?.let {
            logger.audit(
                it,
                "SEND_MASS_EMAILS",
            )
        }
        try {
            val user = request.getAuthenticatedUser() ?: throw Unauthorized()

            val reservations =
                reservationListService.getBoatSpaceReservations(params)
            val recipients = getDistinctRecipients(reservations)

            messageService.sendEmails(
                userId = user.id,
                senderAddress = emailEnv.senderAddress,
                recipients = recipients,
                subject = messageTitle,
                body = messageContent
            )
            return ResponseEntity.ok(
                sendMessageView.renderMessageSentFeedback()
            )
        } catch (e: Exception) {
            logger.error(e) { "error sending message" }
            return ResponseEntity.ok(
                sendMessageView.renderSendingFailed()
            )
        }
    }

    private fun getDistinctRecipients(reservations: PaginatedReservationsResult<BoatSpaceReservationItem>): List<Recipient> {
        val recipients: List<List<Recipient>> =
            reservations.items.map { reservation ->
                val contactDetails = mutableListOf<Recipient>()
                contactDetails.add(Recipient(reservation.reserverId, reservation.email))

                val orgRecipients =
                    if (reservation.reserverType == ReserverType.Organization) {
                        organizationService
                            .getOrganizationMembers(reservation.reserverId)
                            .map { Recipient(it.id, it.email) }
                    } else {
                        emptyList()
                    }
                contactDetails.addAll(orgRecipients)
                contactDetails
            }
        return recipients.flatten().distinctBy { it.id }
    }
}
