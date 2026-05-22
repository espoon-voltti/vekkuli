package fi.espoo.vekkuli.boatSpace.emailAttachments

class MessageSizeLimitExceededException(
    val currentBytes: Long,
    val attemptedBytes: Long,
    val limitBytes: Long,
) : RuntimeException("Attachment combined size limit exceeded")
