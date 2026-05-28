package fi.espoo.vekkuli.boatSpace.emailAttachments

/**
 * Thrown by [AttachmentService.uploadAttachment] when the projected combined size
 * of attachments on a draft would exceed the AWS SES message-size limit.
 *
 * Deliberately not a [fi.espoo.vekkuli.common.BadRequest] / sibling, because
 * the controller catches it locally and renders an HTMX-friendly HTML fragment
 * built from [currentBytes], [attemptedBytes] and [limitBytes]. The structured
 * fields are part of the contract; the project's [fi.espoo.vekkuli.config.ExceptionHandler]
 * only emits a JSON [fi.espoo.vekkuli.config.ErrorResponse], which doesn't fit
 * the composer's UI.
 */
class MessageSizeLimitExceededException(
    val currentBytes: Long,
    val attemptedBytes: Long,
    val limitBytes: Long,
) : RuntimeException("Attachment combined size limit exceeded")
