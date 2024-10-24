package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.EmailEnv
import fi.espoo.vekkuli.controllers.CitizenUserController
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.model.*
import java.util.*

interface SendEmailInterface {
    fun sendMultipleEmails(
        senderAddress: String?,
        emailAddresses: List<String>,
        subject: String,
        body: String
    ): String?
}

@Service
@Profile("test")
class SendEmailServiceMock : SendEmailInterface {
    override fun sendMultipleEmails(
        senderAddress: String?,
        emailAddresses: List<String>,
        subject: String,
        body: String
    ): String? {
        println(
            "Email from $senderAddress to ${emailAddresses.joinToString { ", " }} with subject $subject and content $body"
        )
        return "Test-${UUID.randomUUID()}"
    }
}

@Service
@Profile("!test")
class SendEmailService(
    private val sesClient: SesClient,
    private val emailEnv: EmailEnv
) : SendEmailInterface {
    private val logger = LoggerFactory.getLogger(CitizenUserController::class.java)

    override fun sendMultipleEmails(
        senderAddress: String?,
        emailAddresses: List<String>,
        subject: String,
        body: String
    ): String? {
        if (emailAddresses.isEmpty()) return null
        val uniqueEmails = emailAddresses.distinct()
        if (!emailEnv.enabled) {
            println(
                "Email from $senderAddress (arn ${emailEnv.senderArn}, " +
                    "region ${emailEnv.region}) to ${uniqueEmails.joinToString { ", " }} with subject $subject and content $body"
            )
            return "Test-${UUID.randomUUID()}"
        }

        val emailRequest =
            SendEmailRequest
                .builder()
                .destination(Destination.builder().toAddresses(uniqueEmails).build())
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
            logger.warn("Failed to send email to ${uniqueEmails.joinToString { ", " }}: ${ex.awsErrorDetails().errorMessage()}")
            return null
        }
    }
}
