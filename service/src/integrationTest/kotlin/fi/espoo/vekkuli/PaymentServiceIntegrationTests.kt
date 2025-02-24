package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentServiceIntegrationTests : IntegrationTestBase() {
    @Autowired
    private lateinit var reservationService: BoatReservationService

    @Autowired
    private lateinit var reserverService: ReserverService

    @Autowired private lateinit var paymentService: PaymentService

    @Autowired private lateinit var invoiceService: BoatSpaceInvoiceService

    @BeforeEach
    override fun resetDatabase() {
        deleteAllReservations(jdbi)
    }

    private fun insertNewPayment(ref: String = "1"): Payment {
        val reserver = this.citizenIdLeo
        val reservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                reserver
            )
        return paymentService.insertPayment(
            CreatePaymentParams(reserver, ref, 1, 24.0, "1", PaymentType.OnlinePayment),
            reservationId = reservation.id
        )
    }

    @Test
    fun `should add payment`() {
        val testRef = "testReference"
        val payment = insertNewPayment(testRef)
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(payment.id, fetchedPayment?.id, "Fetched payment ID matches the inserted payment ID")
        assertEquals(testRef, fetchedPayment?.reference, "Fetched payment reference matches the inserted payment reference")
    }

    @Test
    fun `should create payment and link it to reservation`() {
        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )

        val payment =
            reservationService.addPaymentToReservation(
                madeReservation.id,
                CreatePaymentParams(
                    reserverId = this.citizenIdLeo,
                    reference = "1",
                    totalCents = 1,
                    vatPercentage = 24.0,
                    productCode = "1",
                    PaymentType.OnlinePayment
                )
            )
        assertEquals(madeReservation.reserverId, payment.reserverId, "payment is added for correct citizen")
        assertEquals(madeReservation.id, payment.reservationId, "payment is linked to the reservation")
        assertEquals(null, payment.paid, "payment is not set to paid")
    }

    @Test
    fun `payment is created with correct status`() {
        val payment = insertNewPayment()
        assertEquals(PaymentStatus.Created, payment.status, "Payment is created with correct status")
    }

    @Test
    fun `should update payment status on failed payment`() {
        val payment = insertNewPayment()
        paymentService.updatePayment(
            payment.id,
            false,
            null
        )
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(PaymentStatus.Failed, fetchedPayment?.status, "Payment is updated with new status")
    }

    @Test
    fun `should update payment status on successful payment`() {
        val payment = insertNewPayment()
        paymentService.updatePayment(
            payment.id,
            true,
            timeProvider.getCurrentDateTime()
        )
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(PaymentStatus.Success, fetchedPayment?.status, "Payment is updated with new status")
    }

    @Test
    fun `should add invoice`() {
        val reservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                reservationService,
                this.citizenIdLeo
            )
        val invoice =
            testUtils.createInvoiceWithTestParameters(reserverService, invoiceService, timeProvider, this.citizenIdLeo, reservation.id)
        val fetchedInvoice = paymentService.getInvoice(invoice.id)
        assertEquals(invoice.id, fetchedInvoice?.id, "Fetched invoice ID matches the inserted invoice ID")
        assertEquals(invoice, fetchedInvoice, "Fetched invoice matches the inserted invoice")
    }

    @Test
    fun `should set the reservation as paid and set status to confirmed`() {
        val madeReservation =
            testUtils.createReservationInInvoiceState(
                timeProvider,
                reservationService,
                invoiceService,
                this.citizenIdLeo
            )
        reservationService.markInvoicePaid(madeReservation.id, timeProvider.getCurrentDateTime())

        // reservation is marked as paid
        val reservation = reservationService.getBoatSpaceReservation(madeReservation.id)
        assertEquals(timeProvider.getCurrentDate(), reservation?.paymentDate, "Reservation is marked as paid")

        // invoice is created
        val invoice = paymentService.getInvoiceForReservation(madeReservation.id)
        assertNotNull(invoice, "Invoice is found")

        // payment is created with correct status
        val payment = paymentService.getPayment(invoice.paymentId)
        assertEquals(timeProvider.getCurrentDateTime(), payment?.paid, "Fetched invoice payment date is set to today")
        assertEquals(PaymentStatus.Success, payment?.status, "Fetched invoice payment status is set to success")
        assertEquals(reservation?.status, ReservationStatus.Confirmed, "Reservation status is set to confirmed")
    }
}
