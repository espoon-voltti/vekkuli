package fi.espoo.vekkuli.boatSpace.emailAttachments

data class AttachmentData(
    val key: String,
    val contentType: String?,
    val size: Long?,
    val data: ByteArray
)
