package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.EmailEnv
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import java.util.*

@Service
class SendEmailService(
    private val sesClient: SesClient,
    private val emailEnv: EmailEnv
) {
    fun sendEmail(
        senderAddress: String?,
        recipient: String,
        subject: String,
        body: String
    ): String? {
        if (!emailEnv.enabled) {
            println(
                "Email from $senderAddress (arn ${emailEnv.senderArn}, " +
                    "region ${emailEnv.region}) to $recipient with subject $subject and content $body"
            )
            return "Test-${UUID.randomUUID()}"
        }

        val emailRequest =
            SendEmailRequest
                .builder()
                .destination(Destination.builder().toAddresses(recipient).build())
                .sourceArn(emailEnv.senderArn)
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
                ).source(senderAddress)
                .build()
        try {
            val response = sesClient.sendEmail(emailRequest)
            return response.messageId()
        } catch (ex: SesException) {
            println("Failed to send email: ${ex.awsErrorDetails().errorMessage()}")
            return null
        }
    }
}
