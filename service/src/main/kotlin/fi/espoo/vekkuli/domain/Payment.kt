package fi.espoo.vekkuli.domain

import java.time.LocalDate
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
    val reservationId: Int,
    val paid: LocalDateTime?
)

data class Invoice(
    val id: UUID,
    val reference: String,
    val dueDate: LocalDate,
    val citizenId: UUID,
    val paymentId: UUID
)

data class CreatePaymentParams(
    val citizenId: UUID,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
)

data class CreateInvoiceParams(
    val dueDate: LocalDate,
    val reference: String,
    val reservationId: Int,
    val citizenId: UUID,
    val paymentId: UUID,
)
