package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.QueuedMessage
import fi.espoo.vekkuli.domain.Recipient
import fi.espoo.vekkuli.domain.ReservationType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

data class EmailTemplate(
    val id: String,
    val subject: String,
    val body: String,
)

interface EmailTemplateRepository {
    fun getTemplate(templateId: String): EmailTemplate?
}

@Service
class TemplateEmailService(
    private val messageService: MessageService,
    private val templateRepo: EmailTemplateRepository
) {
    private val logger = LoggerFactory.getLogger(TemplateEmailService::class.java)

    fun sendEmail(
        template: String,
        userId: UUID?,
        senderAddress: String,
        recipient: Recipient,
        variables: Map<String, Any>,
    ): List<QueuedMessage> =
        sendBatchEmail(
            template = template,
            userId = userId,
            senderAddress = senderAddress,
            recipients = listOf(recipient),
            variables = variables,
        )

    fun sendBatchEmail(
        template: String,
        userId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        variables: Map<String, Any>,
    ): List<QueuedMessage> {
        val tpl = templateRepo.getTemplate(template)
        if (tpl == null) {
            logger.warn("Email template not found: $template")
            return emptyList()
        }

        return messageService.sendEmails(
            userId = userId,
            senderAddress = senderAddress,
            recipients = recipients,
            subject = replaceTags(tpl.subject, variables),
            body = replaceTags(tpl.body, variables),
        )
    }

    fun sendBatchEmail(
        template: String,
        userId: UUID?,
        senderAddress: String,
        recipients: List<Recipient>,
        reservationType: ReservationType,
        reservationId: Int,
        messageType: String,
        variables: Map<String, Any>,
    ): List<QueuedMessage> {
        val tpl = templateRepo.getTemplate(template)
        if (tpl == null) {
            logger.warn("Email template not found: $template")
            return emptyList()
        }
        // Get emails that have not been sent
        var emails =
            messageService
                .getAndInsertUnsentEmails(reservationType, reservationId, messageType, recipients.map { it.email })
        return messageService.sendEmails(
            userId = userId,
            senderAddress = senderAddress,
            recipients = recipients.filter { emails.contains(it.email) },
            subject = tpl.subject,
            body = replaceTags(tpl.body, variables),
        )
    }

    fun getTemplate(templateId: String): EmailTemplate? = templateRepo.getTemplate(templateId)

    fun replaceTags(
        body: String,
        variables: Map<String, Any>
    ): String {
        val regex = Regex("""\{\{(\s*\w+\s*)}}""")
        return regex.replace(body) {
            variables[it.groupValues[1].trim()]?.toString() ?: it.value
        }
    }
}
