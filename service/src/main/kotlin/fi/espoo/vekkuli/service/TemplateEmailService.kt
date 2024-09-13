package fi.espoo.vekkuli.service

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
        recipientId: UUID,
        recipientEmail: String,
        variables: Map<String, Any>
    ) {
        val tpl = templateRepo.getTemplate(template)
        if (tpl == null) {
            logger.warn("Email template not found: $template")
            return
        }

        messageService.sendEmail(
            userId = userId,
            senderAddress = senderAddress,
            recipientId = recipientId,
            recipientEmail = recipientEmail,
            subject = tpl.subject,
            body = replaceTags(tpl.body, variables),
        )
    }

    private fun replaceTags(
        body: String,
        variables: Map<String, Any>
    ): String {
        val regex = Regex("""\{\{(\s*\w+\s*)}}""")
        return regex.replace(body) {
            variables[it.groupValues[1].trim()]?.toString() ?: it.value
        }
    }
}
