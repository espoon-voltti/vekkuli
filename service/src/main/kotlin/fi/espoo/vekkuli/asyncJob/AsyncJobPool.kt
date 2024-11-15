// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.asyncJob

import io.micrometer.core.instrument.Counter
import mu.KotlinLogging
import java.lang.reflect.UndeclaredThrowableException
import java.time.Duration
import java.util.concurrent.FutureTask
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.reflect.KClass

class AsyncJobPool<T : Any>(
    val id: Id<T>,
    config: Config,
    val registration: Registration<T>,
    private val asyncJobRepository: IAsyncJobRepository
) : AutoCloseable {
    data class Id<T : Any>(
        val jobType: KClass<T>,
        val pool: String
    ) {
        override fun toString(): String = "${jobType.simpleName}.$pool"
    }

    val activeWorkerCount: Int
        get() = executor.activeCount

    private data class Metrics(
        val executedJobs: Counter,
        val failedJobs: Counter
    )

    data class Config(
        val concurrency: Int = 1,
        val throttleInterval: Duration? = null
    )

    data class Handler<T : Any>(
        val handler: (msg: T) -> Unit
    ) {
        fun run(msg: Any) =
            @Suppress("UNCHECKED_CAST")
            handler(msg as T)
    }

    interface Registration<T : Any> {
        fun jobTypes(): Collection<AsyncJobType<out T>>

        fun handlerFor(jobType: AsyncJobType<*>): Handler<*>
    }

    private val fullName: String = "${AsyncJobPool::class.simpleName}.$id"
    private val logger = KotlinLogging.logger("${AsyncJobPool::class.qualifiedName}.$id")
    private val metrics: AtomicReference<Metrics> = AtomicReference()

    val throttleInterval = config.throttleInterval ?: Duration.ZERO
    private val executor =
        config.let {
            val corePoolSize = 1
            val maximumPoolSize = it.concurrency
            val keepAliveTime = Pair(1L, TimeUnit.MINUTES)
            val workQueue = SynchronousQueue<Runnable>()
            val threadNumber = AtomicInteger(1)
            val threadFactory = { r: Runnable ->
                thread(
                    start = false,
                    name = "$fullName.worker-${threadNumber.getAndIncrement()}",
                    priority = Thread.MIN_PRIORITY,
                    block = {
                        try {
                            r.run()
                        } catch (e: Exception) {
                            logger.error(e) { "Error running pool $fullName worker" }
                        }
                    },
                )
            }
            ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime.first,
                keepAliveTime.second,
                workQueue,
                threadFactory,
                ThreadPoolExecutor.DiscardPolicy(),
            )
        }

    fun runPendingJobs(maxCount: Int) = executor.execute { runWorker(maxCount) }

    fun runPendingJobsSync(maxCount: Int): Int {
        val task = FutureTask { runWorker(maxCount) }
        while (!executor.queue.offer(task)) {
            // no available workers
            if (!executor.prestartCoreThread()) {
                // worker capacity full
                TimeUnit.MILLISECONDS.sleep(100)
            }
        }
        return task.get()
    }

    private fun runWorker(maxCount: Int): Int {
        asyncJobRepository.upsertPermit(this.id)
        var executed = 0
        while (maxCount - executed > 0 && !executor.isTerminating) {
            val job =
                asyncJobRepository.temp(this) ?: break
            runPendingJob(job)
            metrics.get()?.executedJobs?.increment()
            executed += 1
        }
        return executed
    }

    private fun runPendingJob(job: ClaimedJobRef<T>) {
        try {
            logger.info { "Running async job $job" }
            val completed =
                asyncJobRepository.temp2(this, job)

            if (completed) {
                logger.info { "Completed async job $job" }
            } else {
                logger.info { "Skipped async job $job due to contention" }
            }
        } catch (e: Throwable) {
            metrics.get()?.failedJobs?.increment()
            val exception = (e as? UndeclaredThrowableException)?.cause ?: e
            logger.error(exception) { "Failed to run async job $job" }
        }
    }

    override fun close() {
        executor.shutdown()
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            logger.error { "Some async jobs did not terminate in time during shutdown" }
        }
        executor.shutdownNow()
    }
}
