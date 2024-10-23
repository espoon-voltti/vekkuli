package fi.espoo.vekkuli

import fi.espoo.vekkuli.domain.CreateInvoiceParams
import fi.espoo.vekkuli.domain.CreatePaymentParams
import fi.espoo.vekkuli.domain.Payment
import fi.espoo.vekkuli.domain.PaymentStatus
import fi.espoo.vekkuli.service.PaymentService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentServiceIntegrationTests : IntegrationTestBase() {
    @BeforeEach
    override fun resetDatabase() {
        deleteAllPayments(jdbi)
    }

    @Autowired
    lateinit var paymentService: PaymentService

    private fun insertNewPayment(ref: String = "1"): Payment =
        paymentService.insertPayment(
            CreatePaymentParams(citizenId, ref, 1, 24.0, "1"),
            reservationId = 1
        )

    @Test
    fun `should add payment`() {
        val testRef = "testReference"
        val payment = insertNewPayment(testRef)
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(payment.id, fetchedPayment?.id, "Fetched payment ID matches the inserted payment ID")
        assertEquals(testRef, fetchedPayment?.reference, "Fetched payment reference matches the inserted payment reference")
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
            false
        )
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(PaymentStatus.Failed, fetchedPayment?.status, "Payment is updated with new status")
    }

    @Test
    fun `should update payment status on successful payment`() {
        val payment = insertNewPayment()
        paymentService.updatePayment(
            payment.id,
            true
        )
        val fetchedPayment = paymentService.getPayment(payment.id)
        assertEquals(PaymentStatus.Success, fetchedPayment?.status, "Payment is updated with new status")
    }

    @Test
    fun `should add invoice`() {
        val invoice =
            paymentService.insertInvoicePayment(
                CreateInvoiceParams(
                    LocalDate.now(),
                    "1",
                    1,
                    citizenId
                ),
            )
        val fetchedInvoice = paymentService.getInvoicePayment(invoice.id)
        assertEquals(invoice.id, fetchedInvoice?.id, "Fetched invoice ID matches the inserted invoice ID")
        assertEquals(invoice, fetchedInvoice, "Fetched invoice matches the inserted invoice")
    }

    @Test
    fun `should set invoice as paid`() {
        val invoice =
            paymentService.insertInvoicePayment(
                CreateInvoiceParams(
                    LocalDate.now(),
                    "1",
                    1,
                    citizenId,
                )
            )
        paymentService.setInvoicePaid(invoice.id)
        val fetchedInvoice = paymentService.getInvoicePayment(invoice.id)
        assertEquals(LocalDate.now(), fetchedInvoice?.paymentDate, "Fetched invoice payment date is set to today")
    }
}
