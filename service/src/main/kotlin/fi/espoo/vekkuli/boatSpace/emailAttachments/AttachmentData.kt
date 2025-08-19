package fi.espoo.vekkuli.boatSpace.emailAttachments

import java.util.UUID

data class AttachmentData(
    val key: String,
    val contentType: String?,
    val size: Long?,
    val data: ByteArray
)
