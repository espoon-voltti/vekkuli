package fi.espoo.vekkuli.boatSpace.emailAttachments

data class AttachmentData(
    val key: String,
    val name: String,
    val contentType: String?,
    val size: Long?,
    val data: ByteArray
)

val allowedContentTypes = listOf("application/pdf", "image/jpeg", "image/jpg", "image/png")
