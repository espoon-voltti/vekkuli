package fi.espoo.vekkuli.domain

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.bindKotlin
import org.jdbi.v3.core.kotlin.mapTo
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
)

data class CreatePaymentParams(
    val citizenId: UUID,
    val reference: String,
    val totalCents: Int,
    val vatPercentage: Double,
    val productCode: String,
)

fun Handle.insertPayment(params: CreatePaymentParams): Payment {
    val id = UUID.randomUUID()
    val result =
        createQuery(
            """
        INSERT INTO payment (id, citizen_id, reference, total_cents, vat_percentage, product_code)
        VALUES (:id, :citizenId,  :reference, :totalCents, :vatPercentage, :productCode)
        RETURNING *
        """
        ).bindKotlin(params)
            .bind("id", id)
            .mapTo<Payment>()
            .one()
    return result
}

// Update payment only if it is 'Created' state
fun Handle.updatePayment(
    id: UUID,
    status: PaymentStatus
): Payment? =
    createQuery(
        """
        UPDATE payment
        SET status = :status, updated = now()
        WHERE id = :id AND status = 'Created'
        RETURNING *
        """
    ).bind("id", id)
        .bind("status", status)
        .mapTo<Payment>()
        .firstOrNull()

fun Handle.handleReservationPaymentResult(
    id: UUID,
    status: PaymentStatus
): String? {
    val payment = updatePayment(id, status)
    if (payment == null) {
        // Payment was updated already
        return null
    }

    if (status == PaymentStatus.Failed) return getBoatSpaceReservationIdForPayment(id)

    val reservationId =
        updateBoatSpaceReservationOnPaymentSuccess(
            id
        )

    // TODO send email to citizen
    return reservationId
}
