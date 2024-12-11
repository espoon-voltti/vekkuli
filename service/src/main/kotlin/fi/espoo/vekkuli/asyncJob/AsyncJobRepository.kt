// SPDX-FileCopyrightText: 2017-2020 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.asyncJob

import fi.espoo.vekkuli.utils.TimeProvider
import mu.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.jdbi.v3.core.qualifier.QualifiedType
import org.jdbi.v3.json.Json
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Repository
class AsyncJobRepository(
    private val jdbi: Jdbi,
    private val timeProvider: TimeProvider
) : IAsyncJobRepository {
    @Transactional
    override fun insertJob(jobParams: JobParams<out Any>): UUID =
        jdbi
            .withHandleUnchecked { handle ->
                handle
                    .createQuery(
                        """
INSERT INTO async_job (type, retry_count, retry_interval, run_at, payload)
VALUES (:jobType, :retryCount, :retryInterval, :runAt, :payload)
RETURNING id
"""
                    ).bind("jobType", AsyncJobType.ofPayload(jobParams.payload).name)
                    .bind("retryCount", jobParams.retryCount)
                    .bind("retryInterval", jobParams.retryInterval)
                    .bind("runAt", jobParams.runAt)
                    .bindByType("payload", jobParams.payload, QualifiedType.of(jobParams.payload.javaClass).with(Json::class.java))
                    .mapTo(UUID::class.java)
                    .one()
            }

    @Transactional
    override fun upsertPermit(pool: AsyncJobPool.Id<*>) {
        jdbi
            .withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        """
INSERT INTO async_job_work_permit (pool_id, available_at)
VALUES (:poolId, 'epoch')
ON CONFLICT DO NOTHING
"""
                    ).bind("poolId", pool.toString())
                    .execute()
            }
    }

    private fun claimPermit(pool: AsyncJobPool.Id<*>): WorkPermit =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createQuery(
                    """
SELECT available_at
FROM async_job_work_permit
WHERE pool_id = :poolId
FOR UPDATE
"""
                ).bind("poolId", pool.toString())
                .mapTo<WorkPermit>()
                .one()
        }

    private fun updatePermit(
        pool: AsyncJobPool.Id<*>,
        availableAt: Instant
    ) {
        jdbi
            .withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        """
UPDATE async_job_work_permit
SET available_at = :availableAt
WHERE pool_id = :poolId
"""
                    ).bind("availableAt", availableAt)
                    .bind("poolId", pool.toString())
                    .execute()
            }
    }

    private fun <T : Any> claimJob(
        now: Instant,
        jobTypes: Collection<AsyncJobType<out T>>,
    ): ClaimedJobRef<T>? =
        jdbi.withHandleUnchecked { handle ->
            handle
                .createUpdate(
                    """
WITH claimed_job AS (
  SELECT id
  FROM async_job
  WHERE run_at <= :now::timestamptz
  AND retry_count > 0
  AND completed_at IS NULL
  AND type = ANY(:jobTypes)
  ORDER BY run_at ASC
  LIMIT 1
  FOR UPDATE SKIP LOCKED
)
UPDATE async_job
SET
  retry_count = greatest(0, retry_count - 1),
  run_at = :now::timestamptz + retry_interval,
  claimed_at = :now::timestamptz,
  claimed_by = txid_current()
WHERE id = (SELECT id FROM claimed_job)
RETURNING id AS jobId, type AS jobType, txid_current() AS txId, retry_count AS remainingAttempts
            """
                ).bind("now", now)
                .bind("jobTypes", jobTypes.map { it.name }.toTypedArray())
                .executeAndReturnGeneratedKeys()
                .map { rs, _ ->
                    val jobType = rs.getString("jobType")
                    val matchedJobType = jobTypes.find { it.name == jobType } ?: error("Job type not found")
                    ClaimedJobRef(
                        jobId = UUID.fromString(rs.getString("jobId")),
                        jobType = matchedJobType as AsyncJobType<T>,
                        txId = rs.getLong("txId"),
                        remainingAttempts = rs.getInt("remainingAttempts")
                    )
                }.findOne()
                .orElse(null)
        }

    override fun <T : Any> startJob(
        job: ClaimedJobRef<T>,
        now: Instant,
    ): T? =
        jdbi
            .withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        """
WITH started_job AS (
  SELECT id
  FROM async_job
  WHERE id = :id
  AND claimed_by = :txId
  FOR UPDATE
)
UPDATE async_job
SET started_at = :now
WHERE id = (SELECT id FROM started_job)
RETURNING payload
"""
                    ).bind("id", job.jobId)
                    .bind("txId", job.txId)
                    .bind("now", now)
                    .executeAndReturnGeneratedKeys("payload")
                    .mapTo(QualifiedType.of(job.jobType.payloadClass.java).with(Json::class.java))
                    .findOne()
                    .orElse(null)
            }

    private fun completeJob(
        job: ClaimedJobRef<*>,
        now: Instant
    ) {
        jdbi
            .withHandleUnchecked { handle ->
                handle
                    .createUpdate(
                        """
UPDATE async_job
SET completed_at = :now
WHERE id = :jobId
"""
                    ).bind("now", now)
                    .bind("jobId", job.jobId)
                    .execute()
            }
    }

    @Transactional
    override fun <T : Any> claimJob(pool: AsyncJobPool<T>): ClaimedJobRef<T>? {
        setStatementTimeout(Duration.ofSeconds(120))
        // In the worst case we need to wait for the duration of (N service
        // instances) * (M workers per pool) * (throttle interval) if every
        // worker in the cluster is queuing and every one sleeps.
        //
        // The value here is just a guess that should be long enough in all
        // valid cases, and we get a loud exception if this assumption is broken
        setLockTimeout(Duration.ofSeconds(60))
        val permit = claimPermit(pool.id)
        val toMillis =
            Duration
                .between(
                    timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC),
                    permit.availableAt
                ).toMillis()
        if (toMillis > 0) {
            logger.info { "Permit claimed, sleeping for $toMillis ms before running" }
            Thread.sleep(
                toMillis
            )
        }
        return claimJob(timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC), pool.registration.jobTypes())?.also {
            updatePermit(pool.id, timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC).plus(pool.throttleInterval))
        }
    }

    @Transactional
    override fun <T : Any> runJob(
        pool: AsyncJobPool<T>,
        job: ClaimedJobRef<out T>
    ): Boolean {
        setLockTimeout(Duration.ofSeconds(5))
        return startJob(job, timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC))?.let { msg ->
            pool.registration.handlerFor(job.jobType).run(msg)
            completeJob(job, timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC))
            true
        } ?: false
    }

    private fun setLockTimeout(duration: Duration) =
        jdbi.withHandleUnchecked {
            it.execute("SET LOCAL lock_timeout = '${duration.toMillis()}ms'")
        }

    private fun setStatementTimeout(duration: Duration) =
        jdbi.withHandleUnchecked { it.execute("SET LOCAL statement_timeout = '${duration.toMillis()}ms'") }

    fun removeCompletedJobs(completedBefore: Instant): Int =
        jdbi.withHandleUnchecked {
            it
                .createUpdate(
                    """
 DELETE FROM async_job
 WHERE completed_at < :completedBefore
 """
                ).bind("completedBefore", completedBefore)
                .execute()
        }

    // todo: handle removing old jobs using these functions
    @Suppress("unused")
    fun removeUnclaimedJobs(jobTypes: Collection<AsyncJobType<*>>): Int =
        jdbi.withHandleUnchecked {
            it
                .createUpdate(
                    """
 DELETE FROM async_job
 WHERE completed_at IS NULL
 AND claimed_at IS NULL
 AND type = ANY(:jobTypes)
    """
                ).bind("jobTypes", jobTypes.map { it.name }.toTypedArray())
                .execute()
        }

    @Suppress("unused")
    fun removeUncompletedJobs(runBefore: Instant): Int =
        jdbi.withHandleUnchecked {
            it
                .createUpdate(
                    """
 DELETE FROM async_job
 WHERE completed_at IS NULL
 AND run_at < :runBefore
 """
                ).bind("runBefore", runBefore)
                .execute()
        }

    @Suppress("unused")
    fun removeOldAsyncJobs(now: Instant) {
        jdbi.withHandleUnchecked {
            val completedBefore = now - Duration.ofDays(180)
            val completedCount = removeCompletedJobs(completedBefore)
            logger.info { "Removed $completedCount async jobs completed before $completedBefore" }

            val runBefore = now - Duration.ofDays(180)
            val oldCount = removeUncompletedJobs(runBefore = runBefore)
            logger.info("Removed $oldCount async jobs originally planned to be run before $runBefore")
        }
    }
}
