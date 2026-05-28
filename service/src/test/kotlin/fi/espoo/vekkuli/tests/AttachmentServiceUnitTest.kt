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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import software.amazon.awssdk.services.s3.S3Client
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

    private fun upload(size: Long) =
        service.uploadAttachment(
            contentType = pdf,
            input = ByteArrayInputStream(ByteArray(0)),
            size = size,
            name = "x.pdf",
        )

    @Test
    fun `upload of any size up to MAX_FILE_SIZE succeeds and persists size`() {
        val id = upload(size = perFileMax)
        assertNotNull(id)
        verify(repo).addAttachment(any(), eq("x.pdf"), eq(perFileMax))
    }

    @Test
    fun `per-file MAX_FILE_SIZE is still enforced`() {
        val ex =
            assertThrows(IllegalArgumentException::class.java) {
                upload(size = perFileMax + 1)
            }
        assertEquals("File size exceeds maximum allowed size", ex.message)
    }

    @Test
    fun `upload no longer rejects based on combined size`() {
        // Even with a "huge" file that's still within per-file but well above the combined cap,
        // upload itself does not throw. The combined-size check now happens at send time.
        val id = upload(size = limit + 1_000_000L)
        assertNotNull(id)
    }

    @Test
    fun `combinedAttachmentSize sums stored sizes`() {
        val a = UUID.randomUUID()
        val b = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(a, b))).thenReturn(mapOf(a to 1_000L, b to 2_000L))
        assertEquals(3_000L, service.combinedAttachmentSize(listOf(a, b)))
    }

    @Test
    fun `combinedAttachmentSize treats NULL size as MAX_FILE_SIZE`() {
        val a = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(a))).thenReturn(mapOf(a to null))
        assertEquals(perFileMax, service.combinedAttachmentSize(listOf(a)))
    }

    @Test
    fun `combinedAttachmentSize treats unknown id as zero`() {
        val a = UUID.randomUUID()
        whenever(repo.findSizesByIds(listOf(a))).thenReturn(emptyMap())
        assertEquals(0L, service.combinedAttachmentSize(listOf(a)))
    }

    @Test
    fun `combinedAttachmentSize on empty input skips repo and returns zero`() {
        assertEquals(0L, service.combinedAttachmentSize(emptyList()))
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
