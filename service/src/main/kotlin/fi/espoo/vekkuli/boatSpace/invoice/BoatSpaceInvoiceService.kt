package fi.espoo.vekkuli.boatSpace.invoice

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.asyncJob.JobParams
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.domain.Invoice
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.CitizenService
import fi.espoo.vekkuli.service.PaymentService
import fi.espoo.vekkuli.utils.TimeProvider
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
    private val citizenService: CitizenService,
    private val reserverRepository: ReserverRepository,
    private val asyncJobRunner: IAsyncJobRunner<AsyncJob>
) {
    @Transactional
    fun createAndSendInvoice(
        invoiceData: InvoiceData,
        citizenId: UUID,
        reservationId: Int
    ): Invoice? {
        val invoice = paymentService.getInvoiceForReservation(reservationId)
        if (invoice != null) {
            return invoice
        }
        val (createdInvoice) = createInvoice(invoiceData, citizenId, reservationId)
        val invoiceDataWithNumber = invoiceData.copy(invoiceNumber = createdInvoice.invoiceNumber)

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
            paymentService.insertInvoice(
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
        reserverId: UUID,
        priceWithVatInCents: Int? = null,
        description: String? = null
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
        val description = description ?: "${reservation.locationName} ${reservation.startDate.year}"

        if (reservation.reserverType == ReserverType.Citizen) {
            val citizen = citizenService.getCitizen(reserverId)
            if (citizen == null) {
                return null
            }
            return InvoiceData(
                type = reservation.type,
                dueDate = timeProvider.getCurrentDate().plusDays(21),
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
                description = description,
            )
        } else {
            return InvoiceData(
                type = reservation.type,
                dueDate = timeProvider.getCurrentDate().plusDays(21),
                startDate = reservation.startDate,
                endDate = reservation.endDate,
                // Organization name
                lastname = reserver.name,
                firstnames = null,
                street = reserver.streetAddress,
                post = reserver.postOffice,
                postalCode = reserver.postalCode,
                language = "fi",
                mobilePhone = reserver.phone,
                email = reserver.email,
                priceCents = price,
                description = description,
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
)
