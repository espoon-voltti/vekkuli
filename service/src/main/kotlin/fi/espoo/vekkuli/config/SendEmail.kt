package fi.espoo.vekkuli.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*

interface EmailSender {
    fun sendEmail(
        sender: String,
        recipient: String,
        subject: String,
        message: String
    ): String?
}

@Configuration
class EmailConfig {
    @Bean
    fun emailSender(): EmailSender =
        object : EmailSender {
            override fun sendEmail(
                sender: String,
                recipient: String,
                subject: String,
                message: String
            ): String? {
                val sesClient =
                    SesClient
                        .builder()
                        .region(Region.EU_WEST_1)
                        .build()

                val emailRequest =
                    SendEmailRequest
                        .builder()
                        .destination(Destination.builder().toAddresses(recipient).build())
                        .message(
                            Message
                                .builder()
                                .subject(Content.builder().data(subject).build())
                                .body(
                                    Body
                                        .builder()
                                        .text(Content.builder().data(message).build())
                                        .build()
                                ).build()
                        ).source(sender)
                        .build()
                try {
                    val response = sesClient.sendEmail(emailRequest)
                    return response.messageId()
                } catch (ex: SesException) {
                    println("Failed to send email: ${ex.awsErrorDetails().errorMessage()}")
                    return null
                } finally {
                    sesClient.close()
                }
            }
        }
}
