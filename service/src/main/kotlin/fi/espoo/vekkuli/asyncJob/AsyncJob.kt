// SPDX-FileCopyrightText: 2017-2020 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.asyncJob

import fi.espoo.vekkuli.boatSpace.invoice.InvoiceData
import java.time.Duration
import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

data class AsyncJobType<T : Any>(
    val payloadClass: KClass<T>
) {
    val name: String = payloadClass.simpleName!!

    override fun toString(): String = name

    companion object {
        fun <T : Any> ofPayload(payload: T): AsyncJobType<T> = AsyncJobType(payload.javaClass.kotlin)
    }
}

sealed interface AsyncJob {
    data class SendInvoiceBatch(
        val invoiceBatch: InvoiceData
    ) : AsyncJob
}

data class JobParams<T>(
    val payload: T,
    val retryCount: Int,
    val retryInterval: Duration,
    val runAt: Instant
)

data class ClaimedJobRef<T : Any>(
    val jobId: UUID,
    val jobType: AsyncJobType<T>,
    val txId: Long,
    val remainingAttempts: Int
)

data class WorkPermit(
    val availableAt: Instant
)
