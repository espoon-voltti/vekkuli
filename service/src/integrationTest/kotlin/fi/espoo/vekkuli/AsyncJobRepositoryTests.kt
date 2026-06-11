// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.AsyncJobPool
import fi.espoo.vekkuli.asyncJob.AsyncJobRepository
import fi.espoo.vekkuli.asyncJob.AsyncJobType
import fi.espoo.vekkuli.asyncJob.setLockTimeout
import fi.espoo.vekkuli.asyncJob.setStatementTimeout
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AsyncJobRepositoryTests : IntegrationTestBase() {
    @Autowired
    private lateinit var asyncJobRepository: AsyncJobRepository

    /**
     * Regression test for the production warning:
     * "SET LOCAL can only be used in transaction blocks".
     *
     * `SET LOCAL` only takes effect inside a transaction. When run on an
     * autocommit connection it is silently ignored (and Postgres warns), so
     * the configured timeout never applies. This asserts the helper actually
     * sets the value within a transaction.
     */
    @Test
    fun `setStatementTimeout applies the timeout within a transaction`() {
        val effective =
            jdbi.inTransactionUnchecked { handle ->
                handle.setStatementTimeout(Duration.ofMillis(1234))
                handle.createQuery("SHOW statement_timeout").mapTo(String::class.java).one()
            }
        assertEquals("1234ms", effective)
    }

    @Test
    fun `setLockTimeout applies the timeout within a transaction`() {
        val effective =
            jdbi.inTransactionUnchecked { handle ->
                handle.setLockTimeout(Duration.ofMillis(2500))
                handle.createQuery("SHOW lock_timeout").mapTo(String::class.java).one()
            }
        assertEquals("2500ms", effective)
    }

    /**
     * Guards the single-transaction refactor of `claimJob`: a planned job is
     * claimed and returned as a [fi.espoo.vekkuli.asyncJob.ClaimedJobRef].
     */
    @Test
    fun `claimJob claims a pending job`() {
        jdbi.withHandleUnchecked { it.execute("DELETE FROM async_job") }

        val poolId = AsyncJobPool.Id(AsyncJob::class, "test")
        val jobType = AsyncJobType(AsyncJob.SendInvoiceBatch::class)
        val registration =
            object : AsyncJobPool.Registration<AsyncJob> {
                override fun jobTypes() = setOf(jobType)

                override fun handlerFor(jobType: AsyncJobType<*>) = AsyncJobPool.Handler<AsyncJob>(handler = {})
            }

        AsyncJobPool(poolId, AsyncJobPool.Config(), registration, asyncJobRepository).use { pool ->
            asyncJobRepository.upsertPermit(pool.id)
            jdbi.withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        """
                        INSERT INTO async_job (type, run_at, retry_count, retry_interval, payload)
                        VALUES (:type, :runAt, 1, 'PT0S', '{}'::jsonb)
                        """.trimIndent()
                    ).bind("type", jobType.name)
                    // far in the past so it is due regardless of the mocked clock
                    .bind("runAt", Instant.EPOCH)
                    .execute()
            }

            val claimed = asyncJobRepository.claimJob(pool)

            assertNotNull(claimed)
            assertEquals(jobType.name, claimed.jobType.name)
        }
    }
}
