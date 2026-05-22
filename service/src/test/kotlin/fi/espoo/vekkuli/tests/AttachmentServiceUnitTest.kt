package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentRepository
import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.boatSpace.emailAttachments.MessageSizeLimitExceededException
import fi.espoo.vekkuli.config.AwsConstants
import fi.espoo.vekkuli.config.EmailEnv
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.util.UUID

class AttachmentServiceUnitTest {
    private lateinit var s3: S3Client
    private lateinit var repo: AttachmentRepository
    private lateinit var emailEnv: EmailEnv
    private lateinit var service: AttachmentService

    private val pdf = "application/pdf"
    private val limit = AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES
    private val perFileMax = AwsConstants.MAX_FILE_SIZE

    @BeforeEach
    fun setUp() {
        s3 = mock()
        repo = mock()
        emailEnv = mock()
        whenever(emailEnv.s3BucketName).thenReturn("test-bucket")
        whenever(repo.addAttachment(any(), any(), any())).thenReturn(UUID.randomUUID())
        service = AttachmentService(s3, repo, emailEnv)
    }

    private fun upload(
        size: Long,
        existing: List<UUID> = emptyList()
    ) = service.uploadAttachment(
        contentType = pdf,
        input = ByteArrayInputStream(ByteArray(0)),
        size = size,
        name = "x.pdf",
        existingAttachmentIds = existing,
    )

    @Test
    fun `first upload at limit succeeds`() {
        val id = upload(size = limit)
        assertNotNull(id)
        verify(repo).addAttachment(any(), eq("x.pdf"), eq(limit))
    }

    @Test
    fun `first upload just over limit is rejected and S3 is not touched`() {
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = limit + 1)
        }
        verify(s3, never()).putObject(any<PutObjectRequest>(), any<software.amazon.awssdk.core.sync.RequestBody>())
        verify(repo, never()).addAttachment(any(), any(), any())
    }

    @Test
    fun `combined size just over limit is rejected`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = 101L, existing = listOf(existingId))
        }
    }

    @Test
    fun `combined size at limit succeeds`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        val id = upload(size = 100L, existing = listOf(existingId))
        assertNotNull(id)
    }

    @Test
    fun `per-file limit still wins over combined limit`() {
        val ex =
            assertThrows(IllegalArgumentException::class.java) {
                upload(size = perFileMax + 1)
            }
        assertEquals("File size exceeds maximum allowed size", ex.message)
    }

    @Test
    fun `existing row with NULL size is treated as MAX_FILE_SIZE`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to null))
        assertThrows(MessageSizeLimitExceededException::class.java) {
            upload(size = 1L, existing = listOf(existingId))
        }
    }

    @Test
    fun `unknown existing id contributes zero`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(emptyMap())
        val id = upload(size = 1L, existing = listOf(existingId))
        assertNotNull(id)
    }

    @Test
    fun `exception carries current attempted and limit bytes`() {
        val existingId = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(existingId))).thenReturn(mapOf(existingId to (limit - 100L)))
        val ex =
            assertThrows(MessageSizeLimitExceededException::class.java) {
                upload(size = 200L, existing = listOf(existingId))
            }
        assertEquals(limit - 100L, ex.currentBytes)
        assertEquals(200L, ex.attemptedBytes)
        assertEquals(limit, ex.limitBytes)
    }

    @Test
    fun `checkMessageSize passes within limit`() {
        service.checkMessageSize("Subject", "Body text", emptyList())
    }

    @Test
    fun `checkMessageSize rejects when body alone over limit`() {
        val hugeBody = "x".repeat((limit + 1).toInt())
        assertThrows(MessageSizeLimitExceededException::class.java) {
            service.checkMessageSize("Subject", hugeBody, emptyList())
        }
    }

    @Test
    fun `checkMessageSize rejects when title plus body plus attachments over limit`() {
        val id = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(id))).thenReturn(mapOf(id to (limit - 100L)))
        val body = "x".repeat(101)
        assertThrows(MessageSizeLimitExceededException::class.java) {
            service.checkMessageSize("", body, listOf(id))
        }
    }

    @Test
    fun `checkMessageSize at limit succeeds`() {
        val id = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(id))).thenReturn(mapOf(id to (limit - 100L)))
        val body = "x".repeat(100)
        service.checkMessageSize("", body, listOf(id))
    }

    @Test
    fun `checkMessageSize uses UTF-8 byte length not char length`() {
        // 'ä' is 2 bytes in UTF-8 but 1 char; (limit/2)+1 chars × 2 bytes/char > limit
        val multiByteContent = "ä".repeat(((limit / 2) + 1).toInt())
        val ex =
            assertThrows(MessageSizeLimitExceededException::class.java) {
                service.checkMessageSize("", multiByteContent, emptyList())
            }
        assertEquals(0L, ex.attemptedBytes)
    }

    @Test
    fun `checkMessageSize exception splits text and attachment bytes`() {
        val id = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(id))).thenReturn(mapOf(id to (limit - 100L)))
        val body = "x".repeat(200)
        val ex =
            assertThrows(MessageSizeLimitExceededException::class.java) {
                service.checkMessageSize("AB", body, listOf(id))
            }
        assertEquals(202L, ex.currentBytes) // 2 (title) + 200 (body)
        assertEquals(limit - 100L, ex.attemptedBytes)
        assertEquals(limit, ex.limitBytes)
    }
}
