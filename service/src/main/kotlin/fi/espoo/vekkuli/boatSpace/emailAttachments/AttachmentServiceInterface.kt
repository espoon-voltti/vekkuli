package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.config.AwsConstants
import fi.espoo.vekkuli.controllers.CitizenUserController
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.util.UUID

interface AttachmentServiceInterface {
    fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long
    ): String?

    fun addAttachmentToMessage(
        messageId: UUID,
        contentType: String,
        input: InputStream,
        size: Long
    ): String?

    fun getAttachmentsForMessage(messageId: UUID): List<AttachmentData>?
}

@Service
@Profile("test")
class AttachmentServiceMock(
    private val attachmentRepository: AttachmentRepository
) : AttachmentServiceInterface {
    override fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long,
    ): String? {
        val key = UUID.randomUUID().toString()
        return key
    }

    override fun addAttachmentToMessage(
        messageId: UUID,
        contentType: String,
        input: InputStream,
        size: Long
    ): String? {
        val key = UUID.randomUUID().toString()
        attachmentRepository.addAttachment(messageId, key)
        return key
    }

    override fun getAttachmentsForMessage(messageId: UUID): List<AttachmentData>? {
        val keys = attachmentRepository.getAttachmentKeys(messageId)

        return keys.map { key ->
            AttachmentData(
                key = key,
                contentType = "text/plain",
                size = 0L,
                data = ByteArray(0) // Mock data, as we don't have actual files in the mock
            )
        }
    }

    data class SentEmail(
        val senderAddress: String,
        val recipientAddress: String,
        val subject: String,
        val body: String
    )

    companion object {
        val emails = mutableListOf<SentEmail>()

        fun resetEmails() {
            emails.clear()
        }
    }
}

@Service
@Profile("!test")
class AttachmentService(
    private val s3Client: S3Client,
    private val attachmentRepository: AttachmentRepository
) : AttachmentServiceInterface {
    private val logger = LoggerFactory.getLogger(CitizenUserController::class.java)

    override fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long,
    ): String {
        val key = "attachment-${System.currentTimeMillis()}.txt"
        if (contentType == null) throw IllegalArgumentException("Content type must not be null")

        // TODO: Validate content type from a list of allowed types
        if (!(
                contentType == "application/pdf" || contentType == "image/jpeg" ||
                    contentType == "image/png"
            )
        ) {
            throw IllegalArgumentException("Content type must be application/pdf or image/jpeg or image/png")
        }
        if (size < 0) throw IllegalArgumentException("Size must not be negative")
        if (size > AwsConstants.MAX_FILE_SIZE) throw IllegalArgumentException("File size exceeds maximum allowed size")
        upload(
            key = key,
            contentType = contentType,
            inputStream = input,
            size = size
        )
        return key
    }

    override fun addAttachmentToMessage(
        messageId: UUID,
        contentType: String,
        input: InputStream,
        size: Long
    ): String? {
        logger.info("Adding attachment for message $messageId")
        val key = "attachment-${System.currentTimeMillis()}.txt"
        upload(
            key = key,
            contentType = contentType,
            inputStream = input,
            size = size
        )
        attachmentRepository.addAttachment(
            messageId = messageId,
            key = key
        )
        return key
    }

    override fun getAttachmentsForMessage(messageId: UUID): List<AttachmentData>? {
        val keys = attachmentRepository.getAttachmentKeys(messageId)
        if (keys.isEmpty()) {
            return null
        }

        return keys.map { key ->
            val response = s3Client.getObject { it.bucket(AwsConstants.BUCKET_NAME).key(key) }
            AttachmentData(
                key = key,
                contentType = response.response().contentType(),
                size = response.response().contentLength(),
                data = response.readAllBytes()
            )
        }
    }

    fun upload(
        key: String,
        contentType: String,
        inputStream: InputStream,
        size: Long,
    ) {
        val request =
            PutObjectRequest
                .builder()
                .bucket("vekkuli-attachments")
                .key(key)
                .contentType(contentType)
                .build()

        val body = RequestBody.fromInputStream(inputStream, size)
        s3Client.putObject(request, body)
    }
}
