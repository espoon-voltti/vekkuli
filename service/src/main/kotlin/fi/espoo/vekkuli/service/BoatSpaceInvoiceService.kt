package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
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
    fun createAndSendInvoice(batch: InvoiceBatchParameters): Invoice? {
        val invoiceParams = batch.invoices.first()
        val (createdInvoice, createdPayment) = createInvoice(invoiceParams)

        val sendInvoiceSuccess = invoiceClient.sendBatchInvoice(batch)
        if (!sendInvoiceSuccess) {
            paymentService.updatePayment(createdPayment.id, false, null)
        }
        return createdInvoice
    }

    fun createInvoice(invoice: InvoiceParameters): Pair<Invoice, Payment> {
        val invoiceRow = invoice.rows[0]

        val payment =
            paymentService.insertPayment(
                CreatePaymentParams(
                    invoice.recipient.id,
                    invoice.invoiceNumber.toString(),
                    invoiceRow
                        .amount
                        .toInt(),
                    BOAT_RESERVATION_ALV_PERCENTAGE,
                    invoiceRow.productId
                ),
                invoiceRow.productId.toInt()
            )
        val invoice =
            paymentService.insertInvoicePayment(
                CreateInvoiceParams(
                    dueDate = invoice.dueDate,
                    reference = invoice.invoiceNumber.toString(),
                    citizenId = invoice.recipient.id,
                    reservationId = invoiceRow.productId.toInt(),
                    paymentId = payment.id
                )
            )
        return Pair(invoice, payment)
    }

    fun createInvoiceBatchParameters(
        reservationId: Int,
        citizenId: UUID,
    ): InvoiceBatchParameters? {
        val reservation =
            boatReservationService.getBoatSpaceReservation(reservationId)
                ?: return null
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
                description = "${reservation.locationName} ${reservation.startDate.year}",
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
