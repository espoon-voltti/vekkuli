package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentController
import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.config.AuthenticatedUser
import fi.espoo.vekkuli.config.AwsConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockMultipartFile
import java.util.UUID

class AttachmentControllerUnitTest {
    private lateinit var service: AttachmentService
    private lateinit var view: AttachmentView
    private lateinit var controller: AttachmentController

    private val limit = AwsConstants.MAX_RAW_ATTACHMENT_TOTAL_BYTES

    @BeforeEach
    fun setUp() {
        service = mock()
        view = mock()
        controller = AttachmentController(service, view)
    }

    private fun authedRequest() =
        MockHttpServletRequest().apply {
            setAttribute("vekkuli.user", AuthenticatedUser(id = UUID.randomUUID(), type = "user"))
        }

    private fun pdf(size: Int = 100) = MockMultipartFile("file", "x.pdf", "application/pdf", ByteArray(size))

    @Test
    fun `successful upload returns 200 with rendered list item`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any())).thenReturn(newId)
        whenever(service.combinedAttachmentSize(any())).thenReturn(100L)
        whenever(view.renderAttachmentListItemWithDelete(eq(newId), eq("x.pdf"))).thenReturn("<li>x.pdf</li>")

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<li>x.pdf</li>", response.body)
    }

    @Test
    fun `under-limit upload sets HX-Trigger with over=false`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any())).thenReturn(newId)
        whenever(service.combinedAttachmentSize(any())).thenReturn(100L)
        whenever(view.renderAttachmentListItemWithDelete(any(), any())).thenReturn("<li/>")

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        val trigger = response.headers.getFirst("HX-Trigger")
        assertNotNull(trigger)
        assertTrue(trigger!!.contains("\"over\":false"), "expected over=false, got: $trigger")
    }

    @Test
    fun `over-limit upload still returns 200 but HX-Trigger over=true with rendered message`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any())).thenReturn(newId)
        whenever(service.combinedAttachmentSize(any())).thenReturn(limit + 1L)
        whenever(view.renderAttachmentListItemWithDelete(any(), any())).thenReturn("<li/>")
        whenever(view.renderSizeLimitError(limit + 1L, limit)).thenReturn("FI: over by 1")

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("<li/>", response.body)
        val trigger = response.headers.getFirst("HX-Trigger")
        assertNotNull(trigger)
        assertTrue(trigger!!.contains("\"over\":true"), trigger)
        assertTrue(trigger.contains("FI: over by 1"), trigger)
    }

    @Test
    fun `combinedAttachmentSize is computed with existing IDs plus the new attachment`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        val existingA = UUID.randomUUID()
        val existingB = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any())).thenReturn(newId)
        whenever(service.combinedAttachmentSize(any())).thenReturn(0L)
        whenever(view.renderAttachmentListItemWithDelete(any(), any())).thenReturn("<li/>")

        controller.addAttachment(
            request = req,
            spaceId = emptyList(),
            existingAttachmentIds = listOf(existingA, existingB),
            file = pdf(),
        )

        verify(service).combinedAttachmentSize(eq(listOf(existingA, existingB, newId)))
    }

    @Test
    fun `generic upload exception returns 400 and no HX-Trigger`() {
        val req = authedRequest()
        whenever(service.uploadAttachment(any(), any(), any(), any())).thenThrow(RuntimeException("boom"))

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNull(response.headers.getFirst("HX-Trigger"))
    }

    @Test
    fun `delete removes path id from draft list and computes remaining total`() {
        val req = authedRequest()
        val deletedId = UUID.randomUUID()
        val keptId = UUID.randomUUID()
        whenever(service.combinedAttachmentSize(any())).thenReturn(0L)

        val response =
            controller.deleteAttachment(
                request = req,
                attachmentId = deletedId,
                draftAttachmentIds = listOf(deletedId, keptId),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("", response.body)
        verify(service).deleteAttachment(deletedId)
        verify(service).combinedAttachmentSize(eq(listOf(keptId)))
        val trigger = response.headers.getFirst("HX-Trigger")
        assertNotNull(trigger)
        assertTrue(trigger!!.contains("\"over\":false"), trigger)
    }

    @Test
    fun `delete leaving still-over-limit set fires over=true trigger`() {
        val req = authedRequest()
        val deletedId = UUID.randomUUID()
        val keptId = UUID.randomUUID()
        whenever(service.combinedAttachmentSize(any())).thenReturn(limit + 1L)
        whenever(view.renderSizeLimitError(limit + 1L, limit)).thenReturn("still over")

        val response =
            controller.deleteAttachment(
                request = req,
                attachmentId = deletedId,
                draftAttachmentIds = listOf(deletedId, keptId),
            )

        val trigger = response.headers.getFirst("HX-Trigger")
        assertNotNull(trigger)
        assertTrue(trigger!!.contains("\"over\":true"), trigger)
        assertTrue(trigger.contains("still over"), trigger)
    }
}
