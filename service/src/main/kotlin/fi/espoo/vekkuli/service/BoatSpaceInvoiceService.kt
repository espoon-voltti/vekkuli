package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.util.*

@Service
class BoatSpaceInvoiceService(
    private val invoiceClient: InvoiceClient,
    private val paymentService: PaymentService,
    private val timeProvider: TimeProvider,
    private val boatReservationService: BoatReservationService,
    private val citizenService: CitizenService
) {
    fun sendInvoice(batch: InvoiceBatchParameters,): Invoice? {
        val sendInvoiceSuccess = invoiceClient.sendBatchInvoice(batch)
        if (!sendInvoiceSuccess) {
            // error handling
            return null
        }
        val invoice = batch.invoices.first()
        return paymentService.insertInvoicePayment(
            CreateInvoiceParams(
                dueDate = invoice.dueDate,
                reference = invoice.invoiceNumber.toString(),
                citizenId = invoice.recipient.id,
                reservationId = invoice.rows[0].productId.toInt(),
            )
        )
    }

    fun createInvoice(
        reservationId: Int,
        citizenId: UUID,
    ): InvoiceBatchParameters? {
        val reservation = boatReservationService.getBoatSpaceReservation(reservationId) ?: return null
        val reserver = citizenService.getCitizen(citizenId) ?: return null
        val invoiceRow =
            Row(
                // TODO: add correct values for productGroup, productComponent, project, description(?) and product
                productGroup = "boat",
                productComponent = "space",
                periodStartDate = reservation.startDate.toString(),
                periodEndDate = reservation.endDate.toString(),
                unitCount = 1,
                unitPrice = reservation.priceCents.toLong(),
                amount = reservation.priceCents.toLong(),
                vatAmount = reservation.alvPriceInCents.toLong(),
                description = "Boat space reservation",
                project = "project",
                product = "boatSpace",
                productId = reservationId.toString()
            )
        val recipient =
            InvoiceRecipient(
                reserver.id,
                reserver.nationalId,
                reserver.firstName,
                reserver.lastName,
                InvoiceAddress(reserver.streetAddress, reserver.postalCode, reserver.postOffice)
            )
        // TODO: calculate due date
        val dueDate = timeProvider.getCurrentDate().plusDays(14)
        val invoice =
            InvoiceParameters(
                // TODO: add correct invoice number
                invoiceNumber = 1,
                dueDate = dueDate,
                recipient = recipient,
                rows = listOf(invoiceRow)
            )
        val batch =
            InvoiceBatchParameters(
                // TODO: add correct values for agreementType, systemId and batchNumber
                agreementType = 249,
                batchDate = timeProvider.getCurrentDate(),
                batchNumber = 1,
                systemId = System.getenv("INVOICE_SYSTEM_ID") ?: "vekkuli",
                invoices = listOf(invoice),
            )
        return batch
    }
}
