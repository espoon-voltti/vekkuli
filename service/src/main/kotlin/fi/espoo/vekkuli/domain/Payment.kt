package fi.espoo.vekkuli.domain

import java.time.LocalDateTime
import java.util.UUID

enum class PaymentStatus {
    Created,
    Success,
    Failed,
}

data class Payment(
    val id: UUID,
    val citizenId: UUID,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val status: PaymentStatus,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
    val reservationId: Int
)

data class CreatePaymentParams(
    val citizenId: UUID,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
)
