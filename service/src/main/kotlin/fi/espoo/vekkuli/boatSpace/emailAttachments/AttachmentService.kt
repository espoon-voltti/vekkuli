package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.config.AwsConstants
import fi.espoo.vekkuli.config.EmailEnv
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.InputStream
import java.util.UUID

@Service
class AttachmentService(
    private val s3Client: S3Client?,
    private val attachmentRepository: AttachmentRepository,
    private val emailEnv: EmailEnv
) {
    fun uploadAttachment(
        contentType: String?,
        input: InputStream,
        size: Long,
        name: String,
    ): UUID {
        val key = "attachment-${UUID.randomUUID()}"
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
        )
        return attachmentRepository.addAttachment(
            key,
            name,
            sizeBytes = size,
        )
    }

    /**
     * Combined raw byte size of the given attachments. NULL stored sizes (legacy rows) are
     * treated conservatively as [AwsConstants.MAX_FILE_SIZE]. Missing IDs contribute 0.
     */
    fun combinedAttachmentSize(attachmentIds: List<UUID>): Long = sumAttachmentSizes(attachmentIds)

    /**
     * Verifies that subject + body + attachments combined stay within the raw-bytes cap
     * that keeps the encoded MIME message under the AWS SES limit. Called at send time
     * to catch the case where attachments are individually under the upload-time cap but
     * a large body pushes the total over.
     *
     * Throws [MessageSizeLimitExceededException] with:
     *  - currentBytes = subject + body bytes (what the user can trim)
     *  - attemptedBytes = sum of attachment bytes (what the user can remove)
     *  - limitBytes = the cap
     */
    fun checkMessageSize(
        messageTitle: String,
        messageContent: String,
        attachmentIds: List<UUID>,
    ) {
        val textBytes = messageTitle.toByteArray(Charsets.UTF_8).size.toLong() + messageContent.toByteArray(Charsets.UTF_8).size.toLong()
        val attachmentTotal = sumAttachmentSizes(attachmentIds)
        if (textBytes + attachmentTotal > AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES) {
            throw MessageSizeLimitExceededException(
                currentBytes = textBytes,
                attemptedBytes = attachmentTotal,
                limitBytes = AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES,
            )
        }
    }

    private fun sumAttachmentSizes(attachmentIds: List<UUID>): Long {
        if (attachmentIds.isEmpty()) return 0L
        return attachmentRepository
            .findSizesByIds(attachmentIds)
            .values
            .sumOf { it ?: AwsConstants.MAX_FILE_SIZE }
    }

    @Transactional
    fun deleteAttachment(id: UUID) {
        val key =
            attachmentRepository.getAttachment(id)?.key
                ?: throw IllegalArgumentException("Attachment not found")
        deleteAttachmentFromS3(key)
        attachmentRepository.deleteAttachment(id)
    }

    fun getAttachment(id: UUID): AttachmentData? {
        val attachment = attachmentRepository.getAttachment(id) ?: return null
        return getAttachmentFromS3(attachment.key, attachment.name)
    }

    private fun getAttachmentFromS3(
        key: String,
        name: String
    ): AttachmentData? {
        val response = s3Client?.getObject { it.bucket(emailEnv.s3BucketName).key(key) }
        if (response == null) {
            return null
        }
        return AttachmentData(
            key = key,
            contentType = response.response().contentType(),
            size = response.response().contentLength(),
            data = response.readAllBytes(),
            name = name
        )
    }

    private fun storeAttachmentToS3(
        key: String,
        contentType: String,
        inputStream: InputStream,
        size: Long
    ) {
        val request =
            PutObjectRequest
                .builder()
                .bucket(emailEnv.s3BucketName)
                .key(key)
                .contentType(contentType)
                .build()

        val body = RequestBody.fromInputStream(inputStream, size)
        s3Client?.putObject(request, body)
    }

    private fun deleteAttachmentFromS3(key: String) {
        val deleted = s3Client?.deleteObject { it.bucket(emailEnv.s3BucketName).key(key) }
        if (deleted === null || !deleted.sdkHttpResponse().isSuccessful) {
            throw RuntimeException("Failed to delete attachment from S3")
        }
    }
}
