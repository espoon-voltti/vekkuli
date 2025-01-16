package fi.espoo.vekkuli.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

enum class PaymentStatus {
    Created,
    Success,
    Failed,
    Refunded
}

enum class PaymentType {
    OnlinePayment,
    Invoice,
    Other
}

data class Payment(
    val id: UUID,
    val reserverId: UUID,
    val paymentType: PaymentType,
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
    val reserverId: UUID,
    val paymentId: UUID,
    val invoiceNumber: Long
)

data class CreatePaymentParams(
    val reserverId: UUID,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
    val paymentType: PaymentType,
    val status: PaymentStatus? = PaymentStatus.Created,
    val paid: LocalDateTime? = null
)

data class CreateInvoiceParams(
    val dueDate: LocalDate,
    val reference: String,
    val reservationId: Int,
    val reserverId: UUID,
    val paymentId: UUID,
)

enum class PaymentMethod {
    Paytrail,
    Invoice
}

data class PaymentHistory(
    val paymentStatus: PaymentStatus,
    val paidDate: LocalDate?,
    val totalCents: Int,
    val harborName: String,
    val place: String,
    val boatSpaceType: BoatSpaceType,
    val paymentReference: String,
    val invoiceReference: String?,
    // val paymentMethod: PaymentMethod,
    val invoiceDueDate: LocalDate?,
    val paymentCreated: LocalDateTime
)
