package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.config.AwsConstants
import fi.espoo.vekkuli.controllers.CitizenUserController
import fi.espoo.vekkuli.domain.Attachment
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.util.UUID

@Service
class AttachmentService(
    private val s3Client: S3Client,
    private val attachmentRepository: AttachmentRepository
) {
    private val logger = LoggerFactory.getLogger(CitizenUserController::class.java)

    fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long,
        name: String
    ): UUID {
        val key = "attachment-${System.currentTimeMillis()}"
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
        storeAttachmentToS3(
            key = key,
            contentType = contentType,
            inputStream = input,
            size = size,
            name = name,
        )
        val id =
            attachmentRepository.addAttachment(
                key,
                name
            )

        return UUID.fromString(id)
    }

    fun deleteAttachment(id: UUID) {
        val key =
            attachmentRepository.getAttachments(listOf(id)).firstOrNull()?.key
                ?: throw IllegalArgumentException("Attachment not found")
        deleteAttachmentFromS3(key)
    }

    fun getAttachments(ids: List<UUID>): List<Attachment> = attachmentRepository.getAttachments(ids)

    fun addAttachmentsToMessage(
        ids: List<UUID>?,
        messageId: List<UUID>
    ) {
        if (ids == null) return
        attachmentRepository.addAttachmentsToMessages(ids, messageId)
    }

    fun getAttachmentsForMessage(messageId: UUID): List<AttachmentData>? {
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
                data = response.readAllBytes(),
                name = response.response().metadata()["name"] ?: "unknown"
            )
        }
    }

    fun getAttachment(id: UUID): AttachmentData? {
        val attachment = attachmentRepository.getAttachment(id) ?: return null
        val response = s3Client.getObject { it.bucket(AwsConstants.BUCKET_NAME).key(attachment.key) }
        return AttachmentData(
            key = attachment.key,
            contentType = response.response().contentType(),
            size = response.response().contentLength(),
            data = response.readAllBytes(),
            name = attachment.name
        )
    }

    private fun storeAttachmentToS3(
        key: String,
        contentType: String,
        inputStream: InputStream,
        size: Long,
        name: String,
    ) {
        val request =
            PutObjectRequest
                .builder()
                .bucket("vekkuli-attachments")
                .key(key)
                .contentType(contentType)
                .metadata(mapOf("name" to name))
                .build()

        val body = RequestBody.fromInputStream(inputStream, size)
        s3Client.putObject(request, body)
    }

    private fun deleteAttachmentFromS3(key: String) {
        s3Client.deleteObject { it.bucket("vekkuli-attachments").key(key) }
    }
}
