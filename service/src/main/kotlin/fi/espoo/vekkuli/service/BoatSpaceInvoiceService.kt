package fi.espoo.vekkuli.service

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.utils.TimeProvider
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class BoatSpaceInvoiceService(
    private val invoiceClient: InvoiceClient,
    private val paymentService: PaymentService,
    private val timeProvider: TimeProvider,
    private val boatReservationService: BoatReservationService,
    private val citizenService: CitizenService
) {
    fun createAndSendInvoice(
        invoiceData: InvoiceData,
        citizenId: UUID,
        reservationId: Int
    ): Invoice? {
        val (createdInvoice, createdPayment) = createInvoice(invoiceData, citizenId, reservationId)
        val sendInvoiceSuccess = invoiceClient.sendBatchInvoice(invoiceData)
        if (!sendInvoiceSuccess) {
            paymentService.updatePayment(createdPayment.id, false, null)
            return null
        }
        return createdInvoice
    }

    fun createInvoice(
        invoiceData: InvoiceData,
        citizenId: UUID,
        reservationId: Int
    ): Pair<Invoice, Payment> {
        val payment =
            paymentService.insertPayment(
                CreatePaymentParams(
                    citizenId,
                    invoiceData.invoiceNumber.toString(),
                    invoiceData.priceCents,
                    BOAT_RESERVATION_ALV_PERCENTAGE,
                    reservationId.toString()
                ),
                reservationId
            )
        val invoice =
            paymentService.insertInvoicePayment(
                CreateInvoiceParams(
                    dueDate = invoiceData.dueDate,
                    reference = invoiceData.invoiceNumber.toString(),
                    citizenId = citizenId,
                    reservationId = reservationId,
                    paymentId = payment.id
                )
            )
        return Pair(invoice, payment)
    }

    fun createInvoiceData(
        reservationId: Int,
        citizenId: UUID,
    ): InvoiceData? {
        val reservation = boatReservationService.getBoatSpaceReservation(reservationId)
        if (reservation == null) {
            // error handling
            return null
        }
        val reserver = citizenService.getCitizen(citizenId) ?: return null

        // TODO: missing some fields
        return InvoiceData(
            dueDate = timeProvider.getCurrentDate().plusDays(21),
            invoiceNumber = 1,
            ssn = reserver.nationalId,
            orgId = "1234567-8",
            registerNumber = "1234567-8",
            lastname = reserver.lastName,
            firstnames = reserver.firstName,
            contactPerson = reserver.firstName,
            street = reserver.streetAddress,
            post = reserver.postOffice,
            postalCode = reserver.postalCode,
            language = "fi",
            mobilePhone = reserver.phone,
            email = reserver.email,
            priceCents = reservation.priceCents,
            vat = reservation.vatCents,
            description = "${reservation.locationName} ${reservation.startDate.year}",
            startDate = reservation.startDate,
            endDate = reservation.endDate
        )
    }
}

data class InvoiceData(
    val dueDate: LocalDate,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val invoiceNumber: Long,
    val ssn: String,
    val orgId: String,
    val registerNumber: String,
    val lastname: String,
    val firstnames: String,
    val contactPerson: String?,
    val street: String,
    val post: String,
    val postalCode: String,
    val language: String,
    val mobilePhone: String,
    val email: String,
    val priceCents: Int,
    val vat: Int,
    val description: String,
)
