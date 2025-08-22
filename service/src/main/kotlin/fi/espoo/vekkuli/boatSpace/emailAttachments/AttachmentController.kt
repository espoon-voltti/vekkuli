package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.audit
import fi.espoo.vekkuli.config.getAuthenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
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

    @PostMapping("/add-attachment", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseBody
    fun addAttachment(
        request: HttpServletRequest,
        @RequestParam spaceId: List<Int>?,
        @RequestParam file: MultipartFile?,
    ): ResponseEntity<String> {
        val authenticatedUser = request.getAuthenticatedUser() ?: throw Unauthorized()

//        authenticatedUser.let {
//            logger.audit(
//                it,
//                "ADD_ATTACHMENT",
//                mapOf(
//                    "reservationIds" to spaceId?.joinToString(", ")
//                )
//            )
//        }
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

    @PostMapping("/delete-attachment/{attachmentId}")
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
            return ResponseEntity.noContent().build()
        } catch (e: Exception) {
            // TODO: handle exceptions
            return ResponseEntity.badRequest().build()
        }
    }
}
