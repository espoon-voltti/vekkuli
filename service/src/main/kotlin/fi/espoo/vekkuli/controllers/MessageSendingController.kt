package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.boatSpace.employeeReservationList.PaginatedReservationsResult
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.SendMessageView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.AuthenticatedUser
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.BoatSpaceReservationFilter
import fi.espoo.vekkuli.domain.BoatSpaceReservationItem
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReserverType
import fi.espoo.vekkuli.service.MessageService
import fi.espoo.vekkuli.service.OrganizationService
import fi.espoo.vekkuli.service.ReserverService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/virkailija/viestit")
class MessageSendingController(
    private val sendMessageView: SendMessageView,
    private val messageService: MessageService,
    private var reservationListService: EmployeeReservationListService,
    private val reserverService: ReserverService,
    private val organizationService: OrganizationService,
    private val emailEnv: EmailEnv,
) {
    private val logger = KotlinLogging.logger {}

    // ensure the messages are sent to all recipients regardless of pagination
    private val paginationEnd = 100000

    @GetMapping("/massa/modal")
    @ResponseBody
    fun sendMassMessageModal(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()
        authenticatedUser.let {
            logger.audit(
                it,
                "OPEN_SEND_MASS_EMAILS_MODAL",
            )
        }
        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }

        val reservations =
            reservationListService.getBoatSpaceReservations(params, 0, paginationEnd)
        val recipients = getDistinctRecipients(reservations)
        return ResponseEntity.ok(
            sendMessageView.renderSendMassMessageModal(reservations.totalRows, recipients.sortedBy { it.email })
        )
    }

    @PostMapping("/massa/laheta")
    @ResponseBody
    fun sendMassEmailMessage(
        request: HttpServletRequest,
        @ModelAttribute params: BoatSpaceReservationFilter,
        @RequestParam("messageTitle") messageTitle: String,
        @RequestParam("messageContent") messageContent: String,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

        authenticatedUser.let {
            logger.audit(
                it,
                "SEND_MASS_EMAILS",
                mapOf(
                    "params" to params.toString()
                )
            )
        }

        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }

        try {
            val reservations =
                reservationListService.getBoatSpaceReservations(params, 0, paginationEnd)
            val recipients = getDistinctRecipients(reservations)

            sendMessage(recipients, authenticatedUser, messageTitle, messageContent)

            return ResponseEntity.ok(sendMessageView.renderMessageSentFeedback(recipients.size))
        } catch (e: Exception) {
            logger.error(e) { "error sending mass message" }
            return ResponseEntity.ok(sendMessageView.renderSendingFailed())
        }
    }

    @GetMapping("/reserver/{reserverId}/modal")
    @ResponseBody
    fun sendMessageToReserverModal(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()
        authenticatedUser.let {
            logger.audit(
                it,
                "OPEN_SEND_EMAIL_TO_RESERVER_MODAL",
                mapOf("reserverId" to reserverId.toString())
            )
        }

        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }
        val recipients = getRecipientsByReserverId(reserverId)
        return ResponseEntity.ok(
            sendMessageView.renderSendMessageToReserverModal(recipients.size, recipients.sortedBy { it.email }, reserverId)
        )
    }

    @PostMapping("/reserver/{reserverId}/laheta")
    @ResponseBody
    fun sendEmailToReserver(
        request: HttpServletRequest,
        @PathVariable reserverId: UUID,
        @RequestParam("messageTitle") messageTitle: String,
        @RequestParam("messageContent") messageContent: String,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

        authenticatedUser.let {
            logger.audit(
                it,
                "SEND_EMAIL_TO_RESERVER",
                mapOf(
                    "reserverId" to reserverId.toString()
                )
            )
        }

        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }
        try {
            val recipients = getRecipientsByReserverId(reserverId)

            sendMessage(recipients, authenticatedUser, messageTitle, messageContent)

            return ResponseEntity.ok(sendMessageView.renderMessageSentFeedback(recipients.size))
        } catch (e: Exception) {
            logger.error(e) { "error sending message to reserver $reserverId" }
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

                if (reservation.reserverType == ReserverType.Organization) {
                    contactDetails.addAll(getOrganizationRecipients(reservation.reserverId))
                }
                contactDetails
            }
        return recipients.flatten().distinctBy { it.id }.filter { it.email.isNotEmpty() }
    }

    private fun getRecipientsByReserverId(reserverId: UUID): List<Recipient> {
        val reserver =
            reserverService.getReserverById(reserverId) ?: throw IllegalArgumentException("Reserver not found with id $reserverId")
        val contactDetails = mutableListOf(Recipient(reserverId, reserver.email))

        if (reserver.type == ReserverType.Organization) {
            contactDetails.addAll(getOrganizationRecipients(reserverId))
        }
        return contactDetails.distinctBy { it.id }.filter { it.email.isNotEmpty() }
    }

    private fun getOrganizationRecipients(reserverId: UUID): List<Recipient> =
        organizationService
            .getOrganizationMembers(reserverId)
            .map { Recipient(it.id, it.email) }

    private fun sendMessage(
        recipients: List<Recipient>,
        user: AuthenticatedUser,
        messageTitle: String,
        messageContent: String
    ) {
        logger.info { "Sending message to ${recipients.size} recipients" }

        messageService.sendEmails(
            userId = user.id,
            senderAddress = emailEnv.senderAddress,
            recipients = recipients,
            subject = messageTitle,
            body = messageContent
        )
    }
}
