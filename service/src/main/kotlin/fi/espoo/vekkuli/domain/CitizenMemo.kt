package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.*

enum class MemoCategory {
    Marine,
    Spaces,
    GroupExercise,
}

data class CitizenMemo(
    val id: Int,
    val createdAt: LocalDateTime,
    val createdBy: UUID?,
    val updatedAt: LocalDateTime?,
    val updatedBy: UUID?,
    val category: MemoCategory,
    val citizenId: UUID,
    val content: String,
)

data class CitizenMemoWithDetails(
    val id: Int,
    val createdAt: LocalDateTime,
    val createdById: UUID?,
    val createdBy: String?,
    val updatedAt: LocalDateTime?,
    val updatedById: UUID?,
    val updatedBy: String?,
    val category: MemoCategory,
    val citizenId: UUID,
    val content: String,
)
