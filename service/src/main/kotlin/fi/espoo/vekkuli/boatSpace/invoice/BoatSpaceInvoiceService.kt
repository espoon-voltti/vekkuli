package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.asyncJob.JobParams
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.config.BoatSpaceConfig.getInvoiceDueDate
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.repository.OrganizationRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.MemoService
import fi.espoo.vekkuli.service.PaymentService
import fi.espoo.vekkuli.service.ReserverService
import fi.espoo.vekkuli.utils.TimeProvider
import fi.espoo.vekkuli.utils.placeTypeToText
import fi.espoo.vekkuli.utils.reservationToText
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

@Service
class BoatSpaceInvoiceService(
    private val paymentService: PaymentService,
    private val timeProvider: TimeProvider,
    private val boatReservationService: BoatReservationService,
    private val reserverService: ReserverService,
    private val reserverRepository: ReserverRepository,
    private val organizationRepository: OrganizationRepository,
    private val asyncJobRunner: IAsyncJobRunner<AsyncJob>,
    private val memoService: MemoService
) {
    @Transactional
    fun createAndSendInvoice(
        invoiceData: InvoiceData,
        reserverId: UUID,
        reservationId: Int,
        employeeId: UUID,
        markAsPaidAndSkipSending: Boolean = false
    ): Invoice? {
        val invoice = paymentService.getInvoiceForReservation(reservationId)
        if (invoice != null) {
            return invoice
        }
        val invoiceDataInput = if (markAsPaidAndSkipSending) invoiceData.copy(priceCents = 0) else invoiceData
        var (createdInvoice) = createInvoice(invoiceDataInput, reserverId, reservationId)

        if (markAsPaidAndSkipSending) {
            // Mark as paid but never send invoice
            boatReservationService.markInvoicePaid(
                reservationId,
                timeProvider.getCurrentDateTime()
            )
        } else {
            val invoiceDataWithNumber = invoiceData.copy(invoiceNumber = createdInvoice.invoiceNumber)

            // Send invoice and continue with normal flow
            asyncJobRunner.plan(
                sequenceOf(
                    JobParams(
                        AsyncJob.SendInvoiceBatch(invoiceDataWithNumber),
                        300,
                        Duration.ofMinutes(3),
                        timeProvider.getCurrentDateTime().toInstant(ZoneOffset.UTC)
                    )
                )
            )
        }
        val reservation =
            boatReservationService.getBoatSpaceReservation(reservationId)
                ?: throw IllegalStateException("Reservation not found for id: $reservationId")

        val memoText = reservationToText(reservation) + if (markAsPaidAndSkipSending) " merkitty maksetuksi" else " luotu lasku"
        memoService.insertMemo(
            reserverId,
            employeeId,
            memoText
        )
        return createdInvoice
    }

    fun createInvoice(
        invoiceData: InvoiceData,
        reserverId: UUID,
        reservationId: Int
    ): Pair<Invoice, Payment> {
        val payment =
            paymentService.insertPayment(
                CreatePaymentParams(
                    reserverId,
                    invoiceData.invoiceNumber?.toString() ?: "",
                    invoiceData.priceCents,
                    BOAT_RESERVATION_ALV_PERCENTAGE,
                    reservationId.toString(),
                    PaymentType.Invoice
                ),
                reservationId
            )

        val invoice =
            paymentService.insertInvoice(
                CreateInvoiceParams(
                    dueDate = invoiceData.dueDate,
                    reference = invoiceData.description,
                    reserverId = reserverId,
                    reservationId = reservationId,
                    paymentId = payment.id
                )
            )

        // Solve chicken and egg problem with payment reference and invoice number
        paymentService.updatePayment(payment.copy(reference = invoice.invoiceNumber.toString()))

        return Pair(invoice, payment)
    }

    fun createInvoiceData(
        reservationId: Int,
        reserverId: UUID,
        priceWithVatInCents: Int? = null,
        description: String? = null,
        function: String? = null,
        contactPerson: String? = null
    ): InvoiceData? {
        val reservation = boatReservationService.getBoatSpaceReservation(reservationId)
        if (reservation == null) {
            return null
        }
        val reserver = reserverRepository.getReserverById(reserverId)
        if (reserver == null) {
            return null
        }

        val price = priceWithVatInCents ?: reservation.priceCents
        val period =
            "${timeProvider.getCurrentDate().year}${if (reservation.startDate.year < reservation.endDate.year) ("-" + reservation.endDate.year) else ""}"
        val spaceType = placeTypeToText(reservation.type)
        val desc =
            description
                ?: "$spaceType $period ${reservation.locationName} ${reservation.place}"

        if (reservation.reserverType == ReserverType.Citizen) {
            val citizen = reserverService.getCitizen(reserverId)
            if (citizen == null) {
                return null
            }
            return InvoiceData(
                type = reservation.type,
                dueDate = getInvoiceDueDate(timeProvider),
                startDate = reservation.startDate,
                endDate = reservation.endDate,
                ssn = citizen.nationalId,
                lastname = citizen.lastName,
                firstnames = citizen.firstName,
                street = citizen.streetAddress,
                post = citizen.postOffice,
                postalCode = citizen.postalCode,
                language = "fi",
                mobilePhone = citizen.phone,
                email = citizen.email,
                priceCents = price,
                description = desc,
                function = function ?: "T1270",
            )
        } else {
            val organization = organizationRepository.getOrganizationById(reserverId)
            return InvoiceData(
                type = reservation.type,
                dueDate = getInvoiceDueDate(timeProvider),
                startDate = reservation.startDate,
                endDate = reservation.endDate,
                // Organization name
                lastname = reserver.name,
                firstnames = null,
                street = organization?.billingStreetAddress ?: reserver.streetAddress,
                post = organization?.billingPostOffice ?: reserver.postOffice,
                postalCode = organization?.billingPostalCode ?: reserver.postalCode,
                language = "fi",
                mobilePhone = reserver.phone,
                email = reserver.email,
                priceCents = price,
                description = desc,
                orgId = organization?.businessId,
                function = function ?: "T1270",
                orgName = organization?.name,
                orgRepresentative = contactPerson ?: organization?.billingName,
            )
        }
    }
}

data class InvoiceData(
    val dueDate: LocalDate,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val invoiceNumber: Long? = null,
    val ssn: String? = null,
    val orgId: String? = null,
    val lastname: String?,
    val firstnames: String?,
    val street: String,
    val post: String,
    val postalCode: String,
    val language: String,
    val mobilePhone: String,
    val email: String,
    val priceCents: Int,
    val description: String,
    val type: BoatSpaceType,
    val orgName: String? = null,
    val function: String? = null,
    val orgRepresentative: String? = null,
)
