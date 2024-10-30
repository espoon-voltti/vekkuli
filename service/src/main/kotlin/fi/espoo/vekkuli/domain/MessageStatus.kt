package fi.espoo.vekkuli.domain

enum class MessageStatus {
    Queued,
    Processing,
    Sent,
    Failed,
}
