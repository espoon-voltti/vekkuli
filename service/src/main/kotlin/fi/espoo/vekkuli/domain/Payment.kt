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
    val total_cents: Int,
    val vat_percentage: Double,
    val product_code: String,
)

data class CreatePaymentParams(
    val citizenId: UUID,
    val reference: String,
    val total_cents: Int,
    val vat_percentage: Double,
    val product_code: String,
)

fun Handle.insertPayment(params: CreatePaymentParams): Payment {
    val id = UUID.randomUUID()
    val result =
        createQuery(
            """
        INSERT INTO payment (id, citizen_id, reference, total_cents, vat_percentage, product_code)
        VALUES (:id, :citizenId,  :reference, :total_cents, :vat_percentage, :product_code)
        RETURNING *
        """
        ).bindKotlin(params)
            .bind("id", id)
            .mapTo<Payment>()
            .one()
    return result
}

fun Handle.updatePayment(
    id: UUID,
    status: PaymentStatus
) {
    createUpdate(
        """
        UPDATE payment
        SET status = :status, updated = now()
        WHERE id = :id
        """
    ).bind("id", id)
        .bind("status", status)
        .execute()
}
