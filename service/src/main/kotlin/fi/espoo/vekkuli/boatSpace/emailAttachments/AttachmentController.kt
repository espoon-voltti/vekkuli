package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.common.NotFound
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Controller
@RequestMapping("/virkailija/viestit")
class AttachmentController(
    private val attachmentService: AttachmentService,
    private val attachmentView: AttachmentView,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/lisaa-liite", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun addAttachment(
        request: HttpServletRequest,
        @RequestParam spaceId: List<Int> = emptyList(),
        @RequestParam file: MultipartFile?,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

        authenticatedUser.let {
            logger.audit(
                it,
                "ADD_ATTACHMENT_STUB",
                mapOf(
                    "reservationIds" to (spaceId.joinToString(", "))
                )
            )
        }
        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }

        try {
            if (file != null) {
                val name = file.originalFilename ?: "unknown"
                val id =
                    attachmentService
                        .uploadAttachment(
                            file.contentType,
                            file.inputStream,
                            file.size,
                            name
                        )
                return ResponseEntity.ok(
                    attachmentView.renderAttachmentListItemWithDelete(
                        id,
                        name
                    )
                )
            } else {
                return ResponseEntity.noContent().build()
            }
        } catch (e: Exception) {
            // TODO: handle exceptions
            return ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/liite/{attachmentId}")
    @ResponseBody
    fun content(
        request: HttpServletRequest,
        @PathVariable attachmentId: UUID,
    ): ResponseEntity<ByteArray?> {
        request.getAuthenticatedUser()?.let {
            logger.audit(it, "OPEN_ATTACHMENT", mapOf("targetId" to attachmentId.toString()))
        }
        try {
            val attachment =
                attachmentService.getAttachment(attachmentId)
                    ?: throw NotFound("Attachment not found for id: $attachmentId")

            return ResponseEntity
                .ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    """inline; filename="${attachment.name}""""
                ).contentType(MediaType.parseMediaType(attachment.contentType ?: "image/jpeg"))
                .contentLength(attachment.size ?: 10L)
                .body(attachment.data)
        } catch (e: Exception) {
            // TODO: handle exceptions
            return ResponseEntity.badRequest().body(null)
        }
    }

    @DeleteMapping("/poista-liite/{attachmentId}")
    @ResponseBody
    fun deleteAttachment(
        request: HttpServletRequest,
        @PathVariable attachmentId: UUID
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()
        authenticatedUser.let {
            logger.audit(
                it,
                "DELETE_ATTACHMENT",
                mapOf(
                    "attachmentId" to attachmentId.toString()
                )
            )
        }
        if (!authenticatedUser.isEmployee()) {
            throw Unauthorized()
        }
        try {
            attachmentService.deleteAttachment(attachmentId)
            return ResponseEntity.ok("")
        } catch (e: Exception) {
            // TODO: handle exceptions
            return ResponseEntity.badRequest().body("Error deleting attachment")
        }
    }
}
