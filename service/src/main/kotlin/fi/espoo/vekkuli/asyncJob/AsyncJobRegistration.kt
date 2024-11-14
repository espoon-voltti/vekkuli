package fi.espoo.vekkuli.asyncJob

import fi.espoo.vekkuli.service.InvoiceClient
import org.springframework.stereotype.Service

@Service
class AsyncJobRegistration(
    asyncJobRunner: AsyncJobRunner<AsyncJob>,
    invoiceClient: InvoiceClient
) {
    init {
        asyncJobRunner.registerHandler<AsyncJob.SendInvoiceBatch> { job -> invoiceClient.sendBatchInvoice(job.invoiceBatch) }
    }
}
