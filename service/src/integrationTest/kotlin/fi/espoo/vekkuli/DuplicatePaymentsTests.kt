package fi.espoo.vekkuli

import fi.espoo.vekkuli.boatSpace.citizenBoatSpaceReservation.ReservationPaymentService
import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.repository.ReserverRepository
import fi.espoo.vekkuli.service.*
import kotlinx.coroutines.runBlocking
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.withHandleUnchecked
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DuplicatePaymentsTests : IntegrationTestBase() {
    @Autowired
    private lateinit var paytrailCacheService: PaytrailCacheService

    @Autowired
    private lateinit var reserverRepository: ReserverRepository

    @Autowired
    private lateinit var reservationPaymentService: ReservationPaymentService

    @Autowired
    private lateinit var paymentService: PaymentService

    @Autowired
    private lateinit var boatSpaceService: BoatSpaceService

    @Autowired
    private lateinit var paymentRepository: PaymentRepository

    @Autowired
    private lateinit var boatService: BoatService

    @Autowired
    private lateinit var reservationService: BoatReservationService

    @MockBean
    private lateinit var paytrail: PaytrailInterface

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
        deleteAllBoatSpaces(jdbi)
        deleteAllBoats(jdbi)
        deleteAllOrganizations(jdbi)
        deleteAllPaytrailCacheResponses(jdbi)
    }

    @Test
    fun `Paytrail cache should return null when transaction id does not have cached response`() {
        val transactionId = "some transaction id"
        val cachedResponse = paytrailCacheService.getPayment(transactionId)
        assertNull(cachedResponse)
    }

    @Test
    fun `Paytrail cache should return cached response`() {
        val transactionId = "some transaction id"
        val response =
            PaytrailPaymentResponse(
                transactionId = transactionId,
                reference = "reference",
                terms = "https://www.paytrail.com",
                providers = listOf()
            )

        paytrailCacheService.putPayment(transactionId, response)
        val cachedResponse = paytrailCacheService.getPayment(transactionId)

        assertEquals(response, cachedResponse)
    }

    @Test
    fun `should create payment for reservation`() {
        val reservation = createReservationInPaymentState()
        val paymentId = addPaymentToReservation(reservation.id)
        val payment = paymentRepository.getPayment(paymentId)

        assertNotNull(payment)
        assertEquals(PaymentStatus.Created, payment.status)
    }

    @Test
    fun `should update payment when one exists in Created state`() {
        val reservation = createReservationInPaymentState()
        val firstPaymentId = addPaymentToReservation(reservation.id)
        val secondPaymentId = addPaymentToReservation(reservation.id)
        val payment = paymentRepository.getPayment(secondPaymentId)

        assertEquals(firstPaymentId, secondPaymentId)
        assertEquals(PaymentStatus.Created, payment?.status)
    }

    @Test
    fun `should fail when payment exists in Success state`() {
        val reservation = createReservationInPaymentState()
        val paymentId = addPaymentToReservation(reservation.id)
        val payment = paymentRepository.updatePaymentStatus(paymentId, true, timeProvider.getCurrentDateTime())

        assertEquals(paymentId, payment?.id)

        assertThrows<Exception> {
            addPaymentToReservation(reservation.id)
        }
    }

    @Test
    fun `should create new payment when payment exists in Failed state`() {
        val reservation = createReservationInPaymentState()
        val firstPaymentId = addPaymentToReservation(reservation.id)
        paymentRepository.updatePaymentStatus(firstPaymentId, false, timeProvider.getCurrentDateTime())

        val secondPaymentId = addPaymentToReservation(reservation.id)
        val payment = paymentRepository.getPayment(secondPaymentId)

        assertNotEquals(firstPaymentId, secondPaymentId)
        assertEquals(PaymentStatus.Created, payment?.status)
    }

    @Test
    fun `should fail when payment exists in Refunded state`() {
        val reservation = createReservationInPaymentState()
        val paymentId = addPaymentToReservation(reservation.id)
        val payment = paymentRepository.getPayment(paymentId) ?: throw RuntimeException("payment not found")
        paymentService.updatePayment(payment.copy(status = PaymentStatus.Refunded))

        assertEquals(paymentId, payment.id)

        assertThrows<Exception> {
            addPaymentToReservation(reservation.id)
        }
    }

    @Test
    fun `should add transaction id to payment`() {
        val expectedTransactionId = "expectedTransactionId"
        val reservation = createReservationInPaymentState()
        val paymentId = addPaymentToReservation(reservation.id)

        val payment = paymentRepository.addTransactionIdToPayment(paymentId, expectedTransactionId)

        assertEquals(expectedTransactionId, payment.transactionId)
    }

    @Test
    fun `should prevent overriding transaction id`() {
        val reservation = createReservationInPaymentState()
        val paymentId = addPaymentToReservation(reservation.id)

        paymentRepository.addTransactionIdToPayment(paymentId, "first transaction id")

        assertThrows<Exception> {
            paymentRepository.addTransactionIdToPayment(paymentId, "second transaction id")
        }
    }

    @Test
    fun `should add transaction id to when adding new payment to reservation`() {
        val expectedTransactionId = "expectedTransactionId"
        val (reservationId) = createReservationInPaymentState()
        val citizen = reserverRepository.getCitizenById(this.citizenIdLeo) ?: throw RuntimeException("citizen not found")
        val reservation = reservationService.getBoatSpaceReservation(reservationId) ?: throw RuntimeException("reservation not found")

        runBlocking {
            setNextNewPaytrailTransactionId(expectedTransactionId)
            reservationPaymentService.createPaymentForBoatSpaceReservation(citizen, reservation)
            val payment = paymentRepository.getPaymentForReservation(reservationId) ?: throw RuntimeException("payment not found")

            assertEquals(PaymentStatus.Created, payment.status)
            assertEquals(expectedTransactionId, payment.transactionId)
        }
    }

    @Test
    fun `should fetch existing Paytrail payment when using existing local payment`() {
        val (reservationId) = createReservationInPaymentState()
        val citizen = reserverRepository.getCitizenById(this.citizenIdLeo) ?: throw RuntimeException("citizen not found")
        val reservation = reservationService.getBoatSpaceReservation(reservationId) ?: throw RuntimeException("reservation not found")

        runBlocking {
            setNextNewPaytrailTransactionId("firstTransactionId")
            val firstResponse = reservationPaymentService.createPaymentForBoatSpaceReservation(citizen, reservation)

            setNextNewPaytrailTransactionId("secondTransactionId")
            val secondResponse = reservationPaymentService.createPaymentForBoatSpaceReservation(citizen, reservation)

            assertEquals(firstResponse.transactionId, secondResponse.transactionId)
        }
    }

    private fun createReservationInPaymentState() =
        testUtils.createReservationInPaymentState(
            timeProvider,
            reservationService,
            this.citizenIdLeo,
            insertBoatSpace(),
            insertBoat(this.citizenIdLeo)
        )

    private fun addPaymentToReservation(reservationId: Int) =
        reservationService
            .upsertCreatedPaymentToReservation(
                reservationId,
                CreatePaymentParams(
                    reserverId = this.citizenIdLeo,
                    reference = "reference",
                    totalCents = 500,
                    vatPercentage = BOAT_RESERVATION_ALV_PERCENTAGE,
                    productCode = "productCode",
                    paymentType = PaymentType.OnlinePayment,
                )
            ).id

    private fun insertBoatSpace(): Int =
        boatSpaceService.createBoatSpace(
            CreateBoatSpaceParams(
                1,
                BoatSpaceType.Storage,
                "A",
                1,
                BoatSpaceAmenity.Trailer,
                100,
                200,
                1,
                true
            )
        )

    private fun insertBoat(
        citizenId: UUID,
        name: String = "TestBoat",
        registrationCode: String = "registrationCode"
    ): Int =
        boatService
            .insertBoat(
                citizenId,
                registrationCode,
                name,
                150,
                150,
                150,
                150,
                BoatType.Sailboat,
                "",
                "",
                OwnershipStatus.Owner
            ).id

    private fun setNextNewPaytrailTransactionId(transactionId: String) {
        Mockito
            .`when`(
                paytrail.createPayment(any())
            ).thenReturn(
                PaytrailPaymentResponse(
                    transactionId = transactionId,
                    reference = "reference",
                    terms = "https://www.paytrail.com",
                    providers = listOf()
                )
            )
    }

    fun deleteAllPaytrailCacheResponses(jdbi: Jdbi) {
        jdbi.withHandleUnchecked { handle ->
            handle.execute("DELETE FROM paytrail_payment_cache")
        }
    }
}
