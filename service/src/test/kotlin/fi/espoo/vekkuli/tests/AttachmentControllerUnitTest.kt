package fi.espoo.vekkuli.tests

import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentController
import fi.espoo.vekkuli.boatSpace.emailAttachments.AttachmentService
import fi.espoo.vekkuli.boatSpace.emailAttachments.MessageSizeLimitExceededException
import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.config.AuthenticatedUser
import org.junit.jupiter.api.Assertions.assertEquals
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

    private fun pdf(size: Int = 100) =
        MockMultipartFile("file", "x.pdf", "application/pdf", ByteArray(size))

    @Test
    fun `over-limit upload returns 422 with rendered size-limit error body`() {
        val req = authedRequest()
        whenever(service.uploadAttachment(any(), any(), any(), any(), any()))
            .thenThrow(MessageSizeLimitExceededException(currentBytes = 1000L, attemptedBytes = 500L, limitBytes = 7_000_000L))
        whenever(view.renderSizeLimitError(1000L, 500L, 7_000_000L))
            .thenReturn("<fake size error fragment>")

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.statusCode)
        assertEquals("<fake size error fragment>", response.body)
    }

    @Test
    fun `generic exception returns 400`() {
        val req = authedRequest()
        whenever(service.uploadAttachment(any(), any(), any(), any(), any()))
            .thenThrow(RuntimeException("boom"))

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = emptyList(),
                file = pdf(),
            )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `existingAttachmentIds list is forwarded to the service`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        val existingA = UUID.randomUUID()
        val existingB = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any(), any())).thenReturn(newId)
        whenever(view.renderAttachmentListItemWithDelete(eq(newId), eq("x.pdf"))).thenReturn("<li/>")

        val response =
            controller.addAttachment(
                request = req,
                spaceId = emptyList(),
                existingAttachmentIds = listOf(existingA, existingB),
                file = pdf(),
            )

        assertEquals(HttpStatus.OK, response.statusCode)
        verify(service).uploadAttachment(
            eq("application/pdf"),
            any(),
            eq(100L),
            eq("x.pdf"),
            eq(listOf(existingA, existingB)),
        )
    }

    @Test
    fun `successful upload returns 200 with rendered list item`() {
        val req = authedRequest()
        val newId = UUID.randomUUID()
        whenever(service.uploadAttachment(any(), any(), any(), any(), any())).thenReturn(newId)
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
}
