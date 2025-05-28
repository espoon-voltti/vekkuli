package fi.espoo.vekkuli.domain

import java.util.*

data class Recipient(
    val id: UUID?,
    val email: String
)

data class ReserverRecipient(
    val organizationRecipients: List<Recipient> = emptyList(),
    val email: String,
    val id: UUID?,
)
