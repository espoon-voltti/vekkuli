package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.controllers.Utils.Companion.isStagingOrProduction
import org.springframework.stereotype.Service
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*

@Service
class SendEmailService {
    fun sendEmail(
        sender: String,
        recipient: String,
        subject: String,
        body: String
    ): String? {
        if (!isStagingOrProduction()) {
            println("Sending email to $recipient with subject $subject and content $body")
            return null
        }

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
                                .text(Content.builder().data(body).build())
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
