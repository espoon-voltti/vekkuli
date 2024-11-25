// SPDX-FileCopyrightText: 2017-2020 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.asyncJob

import fi.espoo.vekkuli.utils.TimeProvider
import mu.KotlinLogging
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.math.max
import kotlin.reflect.KClass

interface IAsyncJobRunner<T : Any> {
    val name: String

    fun <P : T> registerHandler(
        jobType: AsyncJobType<out P>,
        handler: (msg: P) -> Unit,
    )

    fun plan(jobs: Sequence<JobParams<out T>>)

    fun startBackgroundPolling(pollingInterval: Duration = Duration.ofMinutes(1))

    fun stopBackgroundPolling()

    fun runPendingJobsSync(maxCount: Int = 1_000): Int

    fun waitUntilNoRunningJobs(timeout: Duration = Duration.ofSeconds(10))

    fun close()
}

open class AsyncJobRunner<T : Any>(
    payloadType: KClass<T>,
    pools: Iterable<Pool<T>>,
    private val repository: IAsyncJobRepository,
    private val timeProvider: TimeProvider
) : AutoCloseable,
    IAsyncJobRunner<T> {
    data class Pool<T : Any>(
        val id: AsyncJobPool.Id<T>,
        val config: AsyncJobPool.Config,
        val jobs: Set<KClass<out T>>,
    ) {
        fun withThrottleInterval(throttleInterval: Duration?) = copy(config = config.copy(throttleInterval = throttleInterval))
    }

    override val name = "${AsyncJobRunner::class.simpleName}.${payloadType.simpleName}"

    private val logger = KotlinLogging.logger {}
    private val stateLock = ReentrantReadWriteLock()
    private var handlers: Map<AsyncJobType<out T>, AsyncJobPool.Handler<*>> = emptyMap()

    private val pools: List<AsyncJobPool<T>>
    private val jobsPerPool: Map<AsyncJobPool.Id<T>, Set<AsyncJobType<out T>>>
    private val backgroundTimer: AtomicReference<Timer> = AtomicReference()

    init {
        this.jobsPerPool =
            pools.associate { pool -> pool.id to pool.jobs.map { AsyncJobType(it) }.toSet() }
        this.pools =
            pools.map { AsyncJobPool(it.id, it.config, PoolRegistration(it.id), repository) }
    }

    inner class PoolRegistration(
        val id: AsyncJobPool.Id<T>
    ) : AsyncJobPool.Registration<T> {
        override fun jobTypes() = jobsPerPool[id] ?: emptySet()

        override fun handlerFor(jobType: AsyncJobType<*>) =
            stateLock.read { requireNotNull(handlers[jobType]) { "No handler found for $jobType" } }
    }

    private val isBusy: Boolean
        get() = pools.any { it.activeWorkerCount > 0 }

    override fun <P : T> registerHandler(
        jobType: AsyncJobType<out P>,
        handler: (msg: P) -> Unit,
    ): Unit =
        stateLock.write {
            require(jobsPerPool.values.any { it.contains(jobType) }) {
                "No job pool defined for $jobType"
            }
            val ambiguousKey = handlers.keys.find { it.name == jobType.name }
            require(ambiguousKey == null) {
                "handlers for $jobType and $ambiguousKey have a name conflict"
            }

            require(!handlers.containsKey(jobType)) {
                "handler for $jobType has already been registered"
            }
            handlers = handlers + mapOf(jobType to AsyncJobPool.Handler(handler))
        }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun plan(jobs: Sequence<JobParams<out T>>) =
        stateLock.read {
            jobs.forEach { job ->
                val jobType = AsyncJobType.ofPayload(job.payload)
                val id = repository.insertJob(job)
                logger.debug {
                    """
                    $name planned job $jobType(id=$id, runAt=${job.runAt}, retryCount=${job.retryCount}, retryInterval=${job.retryInterval})
                    """.trimIndent()
                }
            }
        }

    override fun startBackgroundPolling(pollingInterval: Duration) {
        val newTimer =
            fixedRateTimer("$name.timer", period = pollingInterval.toMillis()) {
                pools.forEach { it.runPendingJobs(maxCount = 1_000) }
            }
        backgroundTimer.getAndSet(newTimer)?.cancel()
    }

    override fun stopBackgroundPolling() {
        backgroundTimer.getAndSet(null)?.cancel()
    }

    override fun runPendingJobsSync(maxCount: Int): Int {
        var totalCount = 0
        do {
            val executed =
                pools.fold(0) { count, pool ->
                    count + pool.runPendingJobsSync(max(0, maxCount - totalCount - count))
                }
            // A job in one pool may plan a job for some other pool, so we can't just iterate once
            // and assume all jobs were executed. Instead, we're done only when all pools are done
            val done = executed == 0
            totalCount += executed
        } while (!done)
        logger.debug { "$name executed $totalCount jobs synchronously" }
        return totalCount
    }

    override fun waitUntilNoRunningJobs(timeout: Duration) {
        val start = timeProvider.getCurrentDate().atStartOfDay().toInstant(ZoneOffset.UTC)
        val now = timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC)
        do {
            if (!isBusy) return
            TimeUnit.MILLISECONDS.sleep(100)
        } while (Duration.between(start, now).abs() < timeout)
        error { "Timed out while waiting for running jobs to finish" }
    }

    override fun close() {
        stopBackgroundPolling()
        pools.forEach { it.close() }
    }
}

interface IAsyncJobRepository {
    @Transactional
    fun insertJob(jobParams: JobParams<out Any>): UUID

    @Transactional
    fun upsertPermit(pool: AsyncJobPool.Id<*>)

    fun <T : Any> startJob(
        job: ClaimedJobRef<T>,
        now: Instant,
    ): T?

    @Transactional
    fun <T : Any> claimJob(pool: AsyncJobPool<T>): ClaimedJobRef<T>?

    @Transactional
    fun <T : Any> runJob(
        pool: AsyncJobPool<T>,
        job: ClaimedJobRef<out T>
    ): Boolean
}
