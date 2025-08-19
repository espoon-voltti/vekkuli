package fi.espoo.vekkuli.boatSpace.emailAttachments

import fi.espoo.vekkuli.boatSpace.employeeReservationList.components.AttachmentView
import fi.espoo.vekkuli.common.Unauthorized
import fi.espoo.vekkuli.config.getAuthenticatedUser
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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
                val name = file.originalFilename ?: throw IllegalArgumentException("File name cannot be null")
                val id =
                    attachmentService
                        .uploadAttachment(
                            file.contentType,
                            file.inputStream,
                            file.size,
                            name
                        ).toString()
                return ResponseEntity.ok(
                    attachmentView.renderAttachmentListItem(
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
}
