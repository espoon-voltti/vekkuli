package fi.espoo.vekkuli.controllers

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationService
import fi.espoo.vekkuli.boatSpace.employeeReservationList.EmployeeReservationListService
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.SendMessageView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.AuthenticatedUser
import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import fi.espoo.vekkuli.domain.*
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
    private val reserverService: ReserverService,
    private val organizationService: OrganizationService,
    private val emailEnv: EmailEnv,
    private val reservationService: ReservationService,
    private val reservationListService: EmployeeReservationListService,
) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/massa/modal")
    @ResponseBody
    fun sendMassMessageModal(
        request: HttpServletRequest,
        @RequestParam spaceId: List<Int>,
        @ModelAttribute params: BoatSpaceReservationFilter,
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

        val reservationsToSendEmails: List<Int> = getReservationsToSendEmailsTo(spaceId, params)
        val recipients = reservationService.getReservationRecipients(reservationsToSendEmails)

        return ResponseEntity.ok(
            sendMessageView.renderSendMassMessageModal(reservationsToSendEmails.size, recipients.sortedBy { it.email })
        )
    }

    private fun getReservationsToSendEmailsTo(
        spaceId: List<Int>,
        params: BoatSpaceReservationFilter,
    ): List<Int> {
        if (params.selectAll) {
            // If selectAll is true, we ignore the pagination and fetch all recipients
            return reservationListService.getAllBoatSpaceReservations(params).map { it.id }
        }
        return spaceId
    }

    @PostMapping("/massa/laheta")
    @ResponseBody
    fun sendMassEmailMessage(
        request: HttpServletRequest,
        @RequestParam spaceId: List<Int>,
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
                    "reservationIds" to spaceId.joinToString(", ")
                )
            )
        }

        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }

        try {
            val reservationsToSendEmails: List<Int> = getReservationsToSendEmailsTo(spaceId, params)
            val recipients = reservationService.getReservationRecipients(reservationsToSendEmails)

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
