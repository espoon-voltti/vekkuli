package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.UUID

data class SentMessage(
    val id: UUID,
    val providerId: String?,
    val created: LocalDateTime,
    val sentAt: LocalDateTime?,
    val type: MessageType,
    val status: MessageStatus,
    val senderId: UUID?,
    val senderAddress: String?,
    val recipientId: UUID,
    val recipientAddress: String,
    val subject: String,
    val body: String,
)
