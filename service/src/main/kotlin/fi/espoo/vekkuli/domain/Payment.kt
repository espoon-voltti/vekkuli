package fi.espoo.vekkuli.domain

import fi.espoo.vekkuli.boatSpace.invoice.InvoicePaymentWithReservationWarningId
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

data class Price(
    val id: Int,
    val name: String,
    val priceCents: Int,
    val vatCents: Int,
    val netPriceCents: Int,
)

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
    val transactionId: String?,
    val paid: LocalDateTime?
)

data class Invoice(
    val id: UUID,
    val reference: String,
    val dueDate: LocalDate,
    val reserverId: UUID,
    val paymentId: UUID,
    val invoiceNumber: Long,
    val reservationId: Int
)

data class CreatePaymentParams(
    val reserverId: UUID,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
    val paymentType: PaymentType,
    val status: PaymentStatus? = PaymentStatus.Created,
    val paid: LocalDateTime? = null,
    val priceInfo: String? = null
)

data class CreateInvoiceParams(
    val dueDate: LocalDate,
    val reference: String,
    val reservationId: Int,
    val reserverId: UUID,
    val paymentId: UUID,
)

data class PaymentDetails(
    val paymentId: UUID,
    val paymentStatus: PaymentStatus,
    val paidDate: LocalDate?,
    val totalCents: Int,
    val harborName: String,
    val place: String,
    val boatSpaceType: BoatSpaceType,
    val paymentReference: String,
    val invoiceReference: String?,
    val paymentType: PaymentType,
    val invoiceDueDate: LocalDate?,
    val paymentCreated: LocalDateTime,
    val priceInfo: String?
)

data class PaymentHistory(
    val paymentDetails: PaymentDetails,
    val invoicePayments: List<InvoicePaymentWithReservationWarningId>
)
