package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.EMAIL_SENDER
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class TemplateEmailService {
    @Autowired
    lateinit var templateEngine: TemplateEngine

    @Autowired
    lateinit var emailService: SendEmailService

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
        recipient: String,
        subject: String,
        variables: Map<String, Any>
    ) {
        val emailContent = generatePlainTextEmail("email/$template.txt", variables)
        emailService.sendEmail(recipient = recipient, subject = subject, body = emailContent, sender = EMAIL_SENDER)
    }
}
