package fi.espoo.vekkuli.repository

import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Payment
import java.util.*

interface PaymentRepository {
    fun getPayment(stamp: UUID): Payment?

    fun insertPayment(
        params: CreatePaymentParams,
        reservationId: Int
    ): Payment

    fun updatePayment(
        id: UUID,
        success: Boolean
    ): Payment?
}
