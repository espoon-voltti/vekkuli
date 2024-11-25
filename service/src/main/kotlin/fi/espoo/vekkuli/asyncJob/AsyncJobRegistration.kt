package fi.espoo.vekkuli.asyncJob

import fi.espoo.vekkuli.service.InvoiceClient
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service

@Service
class AsyncJobRegistration(
    private val asyncJobRunner: IAsyncJobRunner<AsyncJob>,
    private val invoiceClient: InvoiceClient
) : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        asyncJobRunner.registerHandler(AsyncJobType(AsyncJob.SendInvoiceBatch::class)) { job ->
            invoiceClient.sendBatchInvoice(job.invoiceBatch)
        }
    }
}
