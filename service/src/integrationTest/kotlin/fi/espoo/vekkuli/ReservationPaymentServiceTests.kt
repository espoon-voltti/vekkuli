package fi.espoo.vekkuli

import fi.espoo.vekkuli.config.BoatSpaceConfig.BOAT_RESERVATION_ALV_PERCENTAGE
import fi.espoo.vekkuli.domain.*
import fi.espoo.vekkuli.repository.PaymentRepository
import fi.espoo.vekkuli.service.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReservationPaymentServiceTests : IntegrationTestBase() {
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
    }

    @Test
    fun `should not delete existing payments for reservation`() {
        val reservation = createReservationInPaymentState()
        val firstPaymentId = addPaymentToReservation(reservation.id)
        val secondPaymentId = addPaymentToReservation(reservation.id)
        val firstPayment = paymentRepository.getPayment(firstPaymentId)
        val secondPayment = paymentRepository.getPayment(secondPaymentId)

        assertNotNull(firstPayment)
        assertNotNull(secondPayment)
    }

    @Test
    fun `should abandon existing payments when adding new one`() {
        val reservation = createReservationInPaymentState()
        val firstPaymentId = addPaymentToReservation(reservation.id)
        val secondPaymentId = addPaymentToReservation(reservation.id)
        val firstPayment = paymentRepository.getPayment(firstPaymentId)
        val secondPayment = paymentRepository.getPayment(secondPaymentId)

        assertEquals(PaymentStatus.Abandoned, firstPayment?.status)
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
