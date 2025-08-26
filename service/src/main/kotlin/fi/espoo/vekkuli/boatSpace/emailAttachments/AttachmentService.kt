package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.config.AwsConstants
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
    fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long,
        name: String
    ): UUID {
        val key = "attachment-${System.currentTimeMillis()}"
        if (contentType == null) throw IllegalArgumentException("Content type must not be null")

        if (!allowedContentTypes.contains(contentType)) {
            throw IllegalArgumentException("Content type must be one of: $allowedContentTypes")
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
        return attachmentRepository.addAttachment(
            key,
            name
        )
    }

    fun deleteAttachment(id: UUID) {
        val key =
            attachmentRepository.getAttachment(id)?.key
                ?: throw IllegalArgumentException("Attachment not found")
        deleteAttachmentFromS3(key)
    }

    fun getAttachment(id: UUID): AttachmentData? {
        val attachment = attachmentRepository.getAttachment(id) ?: return null
        val response = s3Client.getObject { it.bucket(AwsConstants.ATTACHMENT_BUCKET_NAME).key(attachment.key) }
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
                .bucket(AwsConstants.ATTACHMENT_BUCKET_NAME)
                .key(key)
                .contentType(contentType)
                .metadata(mapOf("name" to name))
                .build()

        val body = RequestBody.fromInputStream(inputStream, size)
        s3Client.putObject(request, body)
    }

    private fun deleteAttachmentFromS3(key: String) {
        val deleted = s3Client.deleteObject { it.bucket(AwsConstants.ATTACHMENT_BUCKET_NAME).key(key) }
        if (!deleted.sdkHttpResponse().isSuccessful) {
            throw RuntimeException("Failed to delete attachment from S3")
        }
    }
}
