package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.*

data class ReserverMemo(
    val id: Int,
    val createdAt: LocalDateTime,
    val createdBy: UUID?,
    val updatedAt: LocalDateTime?,
    val updatedBy: UUID?,
    val category: ReservationType,
    val reserverId: UUID,
    val content: String,
)

data class ReserverMemoWithDetails(
    val id: Int,
    val createdAt: LocalDateTime,
    val createdById: UUID?,
    val createdBy: String?,
    val updatedAt: LocalDateTime?,
    val updatedById: UUID?,
    val updatedBy: String?,
    val category: ReservationType,
    val reserverId: UUID,
    val content: String,
)
