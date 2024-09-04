package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.EMAIL_SENDER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*

@Service
class TemplateEmailService {
    @Autowired
    lateinit var templateEngine: TemplateEngine

    @Autowired
    lateinit var messageService: MessageService

    fun generatePlainTextEmail(
        template: String,
        variables: Map<String, Any>
    ): String {
        val context =
            Context().apply {
                setVariables(variables)
            }
        return templateEngine.process(template, context)
    }

    fun sendEmail(
        template: String,
        userId: UUID?,
        recipientId: UUID,
        recipientEmail: String,
        subject: String,
        variables: Map<String, Any>
    ) {
        val emailContent = generatePlainTextEmail("email/$template.txt", variables)
        messageService.sendEmail(
            userId = userId,
            senderAddress = EMAIL_SENDER,
            recipientId = recipientId,
            recipientEmail = recipientEmail,
            subject = subject,
            body = emailContent,
        )
    }
}
