package fi.espoo.vekkuli

import fi.espoo.vekkuli.asyncJob.AsyncJob
import fi.espoo.vekkuli.asyncJob.IAsyncJobRunner
import fi.espoo.vekkuli.boatSpace.invoice.BoatSpaceInvoiceService
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BoatSpaceInvoiceServiceTests : IntegrationTestBase() {
    @Autowired
    lateinit var boatSpaceInvoiceService: BoatSpaceInvoiceService

    @Autowired
    lateinit var boatReservationService: BoatReservationService

    @Autowired
    lateinit var invoiceService: BoatSpaceInvoiceService

    @MockBean
    lateinit var asyncJobRunner: IAsyncJobRunner<AsyncJob>

    @BeforeEach
    override fun resetDatabase() {
        deleteAllInvoices(jdbi)
    }

    @Test
    fun `should create invoice with correct parameters`() {
        val madeReservation =
            testUtils.createReservationInConfirmedState(
                CreateReservationParams(
                    timeProvider,
                    this.citizenIdLeo
                )
            )
        val invoiceData =
            boatSpaceInvoiceService.createInvoiceData(
                madeReservation.id,
                this.citizenIdLeo
            )
        assertNotNull(invoiceData, "Invoice is created")
        assertEquals(26719, invoiceData?.priceCents, "Price is correct")
        val invoice =
            boatSpaceInvoiceService.createAndSendInvoice(
                invoiceData!!,
                this.citizenIdLeo,
                madeReservation.id
            )

        verify(asyncJobRunner).plan(any())
        assertNotNull(invoice, "Invoice is sent")
        assertEquals(
            this.citizenIdLeo,
            invoice!!.reserverId,
            "Invoice is sent to correct citizen"
        )
    }

    @Test
    fun `should send invoice`() {
        val madeReservation =
            testUtils.createReservationInPaymentState(
                timeProvider,
                boatReservationService,
                this.citizenIdLeo
            )
        val invoiceBatchParameters =
            boatSpaceInvoiceService.createInvoiceData(
                madeReservation.id,
                this.citizenIdLeo
            )
        val invoice = boatSpaceInvoiceService.createAndSendInvoice(invoiceBatchParameters!!, this.citizenIdLeo, madeReservation.id)
        assertNotNull(invoice, "Invoice is sent")
        assertEquals(this.citizenIdLeo, invoice!!.reserverId, "Invoice is sent to correct citizen")
        val reservation = boatReservationService.getBoatSpaceReservation(madeReservation.id)
        assertNull(reservation?.paymentDate, "Reservation has not been paid yet")
    }
}
