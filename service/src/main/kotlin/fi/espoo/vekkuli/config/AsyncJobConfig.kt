// SPDX-FileCopyrightText: 2017-2020 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package fi.espoo.vekkuli.config

import fi.espoo.vekkuli.asyncJob.*
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AsyncJobConfig {
    @Bean
    fun asyncJobRunner(repository: IAsyncJobRepository): IAsyncJobRunner<AsyncJob> =
        AsyncJobRunner(
            AsyncJob::class,
            listOf(
                AsyncJobRunner.Pool(
                    AsyncJobPool.Id(AsyncJob::class, "invoice"),
                    AsyncJobPool.Config(concurrency = 1),
                    setOf(
                        AsyncJob.SendInvoiceBatch::class,
                    ),
                )
            ),
            repository
        )

    @Bean
    fun asyncJobRunnerStarter(asyncJobRunners: List<IAsyncJobRunner<*>>) =
        ApplicationListener<ApplicationReadyEvent> {
            val logger = KotlinLogging.logger {}
            asyncJobRunners.forEach {
                it.startBackgroundPolling()
                logger.info("Async job runner ${it.name} started")
            }
        }
}
