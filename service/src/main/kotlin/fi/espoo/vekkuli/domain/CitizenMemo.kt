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
    val createdAt: LocalDateTime,
    val createdBy: UUID,
    val updatedAt: LocalDateTime,
    val updatedBy: UUID,
    val category: MemoCategory,
    val citizenId: UUID,
    val content: String,
)
