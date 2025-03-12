package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.invoice.*
import fi.espoo.vekkuli.repository.InvoicePaymentRepository
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.service.BoatReservationService
import fi.espoo.vekkuli.service.PaymentService
import fi.espoo.vekkuli.service.ReservationWarningRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class InvoicePaymentServiceIntegrationTest : IntegrationTestBase() {
    lateinit var sut: InvoicePaymentService

    @Autowired
    lateinit var mockInvoicePaymentClient: MockInvoicePaymentClient

    @Autowired
    lateinit var reservationService: BoatReservationService

    @Autowired lateinit var boatSpaceInvoiceService: BoatSpaceInvoiceService

    @Autowired
    lateinit var invoicePaymentRepository: InvoicePaymentRepository

    @Autowired
    lateinit var paymentRepository: PaymentRepository

    @Autowired
    lateinit var reservationWarningRepository: ReservationWarningRepository

    @Autowired
    private lateinit var paymentService: PaymentService

    @BeforeEach
    fun setup() {
        deleteAllReservations(jdbi)
        sut =
            InvoicePaymentService(
                mockInvoicePaymentClient,
                invoicePaymentRepository,
                paymentRepository,
                reservationWarningRepository
            )
    }

    @Test
    fun `fetch and store invoice payments`() {
        val reservation =
            testUtils.createReservationInInvoiceState(
                timeProvider,
                reservationService,
                boatSpaceInvoiceService,
                citizenIdOlivia,
                2,
                3
            )

        val invoice = paymentService.getInvoiceForReservation(reservation.id)!!

        MockInvoicePaymentClient.payments.add(
            Receipt(1, BigDecimal(20.21), "2025-01-01", "VEK_${invoice.invoiceNumber}")
        )

        sut.fetchAndStoreInvoicePayments()
        val payments = sut.getInvoicePayments(invoice.invoiceNumber)
        assertThat(payments.count()).isEqualTo(1)
        val payment = payments.first()
        assertThat(payment.amountPaidCents).isEqualTo(2021)
        assertThat(payment.paymentDate).isEqualTo("2025-01-01")
        assertThat(payment.transactionNumber).isEqualTo(1)
        assertThat(payment.invoiceNumber).isEqualTo(invoice.invoiceNumber)
    }
}
