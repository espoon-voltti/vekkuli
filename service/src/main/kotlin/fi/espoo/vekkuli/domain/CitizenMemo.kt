package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.*

enum class MemoCategory {
    Marine,
    Room,
    Groups,
}

data class CitizenMemo(
    val id: Int,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val category: MemoCategory,
    val userId: UUID,
    val citizenId: UUID,
    val content: String,
)
