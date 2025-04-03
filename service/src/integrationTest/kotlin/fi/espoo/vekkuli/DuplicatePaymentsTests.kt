package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class DuplicatePaymentsTests : IntegrationTestBase() {
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

    @BeforeEach
    override fun resetDatabase() {
        PaytrailMock.reset()
        deleteAllReservations(jdbi)
        deleteAllBoatSpaces(jdbi)
        deleteAllBoats(jdbi)
        deleteAllOrganizations(jdbi)
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
    fun `should create new payment when payment exists in Refunded state`() {
        val reservation = createReservationInPaymentState()
        val firstPaymentId = addPaymentToReservation(reservation.id)
        val firstPayment = paymentRepository.getPayment(firstPaymentId) ?: throw RuntimeException("payment not found")
        paymentService.updatePayment(firstPayment.copy(status = PaymentStatus.Refunded))

        val secondPaymentId = addPaymentToReservation(reservation.id)
        val secondPayment = paymentRepository.getPayment(secondPaymentId)

        assertNotEquals(firstPaymentId, secondPaymentId)
        assertEquals(PaymentStatus.Created, secondPayment?.status)
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
            .addPaymentToReservation(
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
}
