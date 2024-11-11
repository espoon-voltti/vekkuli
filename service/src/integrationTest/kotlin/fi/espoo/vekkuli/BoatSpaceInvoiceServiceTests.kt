package fi.espoo.vekkuli

import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
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
    lateinit var invoiceClientMock: InvoiceClient

    @BeforeEach
    override fun resetDatabase() {
        deleteAllInvoices(jdbi)
        whenever(invoiceClientMock.sendBatchInvoice(any())).thenReturn(true)
    }

    @Test
    fun `should create invoice with correct parameters`() {
        val madeReservation =
            createReservationInInvoiceState(
                timeProvider,
                boatReservationService,
                invoiceService,
                this.citizenIdLeo
            )
        val invoiceBatchParameters =
            boatSpaceInvoiceService.createInvoiceBatchParameters(
                madeReservation.id,
                this.citizenIdLeo
            )
        assertNotNull(invoiceBatchParameters, "Invoice is created")
        assertEquals(26719, invoiceBatchParameters!!.invoices[0].rows[0].amount, "Price is correct")
        assertEquals(
            madeReservation.id.toString(),
            invoiceBatchParameters.invoices[0].rows[0].productId,
            "Reservation id is used as productId"
        )
        val invoice =
            boatSpaceInvoiceService.createAndSendInvoice(
                invoiceBatchParameters!!
            )
        verify(invoiceClientMock).sendBatchInvoice(
            eq(invoiceBatchParameters)
        )
        assertNotNull(invoice, "Invoice is sent")
        assertEquals(
            this.citizenIdLeo,
            invoice!!.citizenId,
            "Invoice is sent to correct citizen"
        )
    }

    @Test
    fun `should send invoice`() {
        val madeReservation =
            createReservationInPaymentState(
                timeProvider,
                boatReservationService,
                this.citizenIdLeo
            )
        val invoiceBatchParameters =
            boatSpaceInvoiceService.createInvoiceBatchParameters(
                madeReservation.id,
                this.citizenIdLeo
            )
        val invoice = boatSpaceInvoiceService.createAndSendInvoice(invoiceBatchParameters!!)
        assertNotNull(invoice, "Invoice is sent")
        assertEquals(this.citizenIdLeo, invoice!!.citizenId, "Invoice is sent to correct citizen")
        val reservation = boatReservationService.getBoatSpaceReservation(madeReservation.id)
        assertNull(reservation?.paymentDate, "Reservation has not been paid yet")
    }
}
